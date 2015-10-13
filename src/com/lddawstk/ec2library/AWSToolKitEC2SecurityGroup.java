package com.lddawstk.ec2library;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.lddawstk.library.ErrorManager;

//http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-network-security.html

public class AWSToolKitEC2SecurityGroup
{
	private AmazonEC2Client ec2;
	private ErrorManager errorManager = null;
	
	public AWSToolKitEC2SecurityGroup()
	{
		this.ec2 = AmazonEC2ClientWrapper.getInstance();
		this.errorManager = new ErrorManager();
	}
	
	public ErrorManager getErrorInformation()
	{
		return this.errorManager;
	}
	
	/*
	 * This function will add a security group + assign permission.
	 */
	public String createSecurityGroup(String groupName, String groupDesc)
	{
		String groupId = "";
        try
        {
            CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(groupName, groupDesc);
            CreateSecurityGroupResult result = this.ec2.createSecurityGroup(securityGroupRequest);
            groupId = result.getGroupId();
            return groupId;        
        }
        catch (AmazonServiceException e)
        {
        	this.errorManager.setErrorMessage(e.getMessage());
        	return groupId;
        }
        
	}
	
	public void deleteSecurityGroup(String groupId)
	{
		DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest();
		request.setGroupId(groupId);
		ec2.deleteSecurityGroup(request);
	}
	
	public boolean assignPortsToSecurityGroup(String groupName, String[] allowedIPRanges, int[] allowedTCPPorts, int[] allowedUDPPorts)
	{
        //CONVERT ALLOWED IP RANGES TO LIST<>.
        List<String> ipRanges = new ArrayList<String>();
        for(String i : allowedIPRanges)
        {
        	ipRanges.add(i);
        }

        //BUILD LIST OF ALLOWED PORTS
        List<Integer> tcpPortRanges = new ArrayList<Integer>();
        List<Integer> udpPortRanges = new ArrayList<Integer>();
        for(int i: allowedTCPPorts)
        {
        	tcpPortRanges.add(i);
        }
        for(int i: allowedUDPPorts)
        {
        	udpPortRanges.add(i);
        }
        
        //BUILD LIST OF IP PERMISSIONS, USING IP RANGE + PORT
        List<IpPermission> ipPermissions = new ArrayList<IpPermission>();
        for(Integer i : tcpPortRanges)
        {
            IpPermission ipPermission = new IpPermission()
                    .withIpProtocol("tcp")
                    .withFromPort(new Integer(i))
                    .withToPort(new Integer(i))
                    .withIpRanges(ipRanges);
            ipPermissions.add(ipPermission);
        }
        for(Integer i : udpPortRanges)
        {
            IpPermission ipPermission = new IpPermission()
                    .withIpProtocol("udp")
                    .withFromPort(new Integer(i))
                    .withToPort(new Integer(i))
                    .withIpRanges(ipRanges);
            ipPermissions.add(ipPermission);
        }
        

        try
        {
            // Authorize the ports to the used.
            AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(groupName, ipPermissions);
            ec2.authorizeSecurityGroupIngress(ingressRequest);
            return true;
        }
        catch (AmazonServiceException e)
        {
        	this.errorManager.setErrorMessage(e.getMessage());
            return false;
        }
	}
	
	/*
	 * This function will return a List<> of all our SecurityGroup's.
	 * 
	 * @return List<SecurityGroup>
	 */
	public List<SecurityGroup> getSecurityGroupList()
	{
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        DescribeSecurityGroupsResult describeSecurityGroupRequest = ec2.describeSecurityGroups();
        List<SecurityGroup> securityGroupList = describeSecurityGroupRequest.getSecurityGroups();
        return securityGroupList;
	}
	
	
	
}
