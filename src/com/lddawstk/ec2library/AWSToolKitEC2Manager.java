package com.lddawstk.ec2library;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.opsworks.model.DeleteInstanceRequest;

public class AWSToolKitEC2Manager
{
	private AmazonEC2Client ec2;
	private AWSToolKitEC2Utility ec2Util;
	private AWSToolKitEC2SecurityGroup securityGroup;
	private AWSToolKitEC2SecurityGroupUtil securityGroupUtil;
	
	public AWSToolKitEC2Manager()
	{
		this.ec2 = AmazonEC2ClientWrapper.getInstance();
		this.ec2Util = new AWSToolKitEC2Utility();
        this.securityGroup = new AWSToolKitEC2SecurityGroup();
        this.securityGroupUtil = new AWSToolKitEC2SecurityGroupUtil();
	}
	
	/*
	 * This function will return a list of the various EC2 Instance Types we can use [T1Micro, M1Small, etc]
	 * 
	 * @return List<String>
	 */
	public List<String> getInstanceList()
	{
		List<InstanceType> instanceList = this.ec2Util.getInstanceList();
		List<String> retList = new ArrayList<String>();
		
		for(InstanceType i : instanceList)
		{
			retList.add(i.name());
		}
		
		return retList;
	}
	
	/*
	 * This function will return a list of All EC2 Amazon Machine Images in our Specific Region
	 * 
	 * @return List<AMIDSO>
	 */
	public List<EC2AMIDSO> getAMIList()
	{
		List<EC2AMIDSO> amiList= this.ec2Util.getAMIList();
		return amiList;
	}
	
	/*
	 * This function will return a list of All EC2 Amazon Security Groups.
	 * 
	 * @return List<SecurityGroupDSO>
	 */
	public List<EC2SecurityGroupDSO> getSecurityGroupList()
	{
		List<EC2SecurityGroupDSO> secGroupDSO = new ArrayList<EC2SecurityGroupDSO>();
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        DescribeSecurityGroupsResult describeSecurityGroupRequest = this.ec2.describeSecurityGroups();
        List<SecurityGroup> securityGroupList = describeSecurityGroupRequest.getSecurityGroups();
        
        for(SecurityGroup i : securityGroupList)
        {
        	List<IpPermission> ipPerm = i.getIpPermissions();
        	EC2SecurityGroupDSO model = new EC2SecurityGroupDSO();
        	model.groupId = i.getGroupId();
        	model.groupName = i.getGroupName();
        	model.description = i.getDescription();
        	model.vpcId = i.getVpcId();
        	model.tags = i.getTags().toString();    	
        	
        	secGroupDSO.add(model);
        }
        return secGroupDSO;
	}
	
	public String createKeyPair(String keyName) throws Exception
	{
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
		createKeyPairRequest.setKeyName(keyName);
		
		try
		{
			CreateKeyPairResult result = this.ec2.createKeyPair(createKeyPairRequest);
			String privateKeyPEMEncoded = result.getKeyPair().getKeyMaterial();
			return privateKeyPEMEncoded;
		}
		catch(AmazonServiceException e)
		{
			throw new Exception("createKeyPair Exception: " + e.getMessage());
		}
	}
	
	public boolean deleteKeyPair(String keyName) throws Exception
	{
		DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest();
		deleteKeyPairRequest.setKeyName(keyName);
		
		try
		{
			this.ec2.deleteKeyPair(deleteKeyPairRequest);
			return true;
		}
		catch(AmazonServiceException e)
		{
			throw new Exception("deleteKeyPair Exception: " + e.getMessage());
		}
	}
	
	/*
	 * This function will create a new EC2 Instance.
	 * 
	 * @param	instanceType		Specifies instance type [t2.micro, etc]
	 * @param	keyName				The key pair to use.
	 * @param	imageId				The Amazon Machine Image to use.
	 * @param	launchMinInstances	The min EC2 Instances to Launch
	 * @param	launchMaxInstances	The max EC2 Instances to Launch
	 * @return	List<Instance>
	 */
	public List<Instance> createEC2Instance(String instanceType, String keyName, String imageId, int launchMinInstances, int launchMaxInstances) throws Exception
	{
		RunInstancesRequest runInstanceRequest = new RunInstancesRequest();
		runInstanceRequest.withInstanceType("t2.micro");
		runInstanceRequest.withKeyName(keyName);
		runInstanceRequest.withImageId("ami-0d4cfd66");
		runInstanceRequest.withMinCount(launchMinInstances);				//NUM OF MIN INSTANCES TO LAUNCH
		runInstanceRequest.withMaxCount(launchMaxInstances);				//NUM OF MAX INSTANCES TO LAUNCH
		
		try
		{
			RunInstancesResult runInstances = this.ec2.runInstances(runInstanceRequest);
			List<Instance> instanceList = runInstances.getReservation().getInstances();
			return instanceList;
		}
		catch(AmazonServiceException e)
		{
			throw new Exception("createEC2Instance Exception: " + e.getMessage());
		}
	}
	
	public void deleteEC2Instance(String instanceId ) throws Exception
	{
		DeleteInstanceRequest request = new DeleteInstanceRequest();
		request.setInstanceId(instanceId);
		
		try
		{
			request.setInstanceId(instanceId);
		}
		catch(AmazonServiceException e)
		{
			throw new Exception("deleEC2Instance Exception: " + e.getMessage());
		}
	}
	
	public List<Instance> getEC2InstanceList()
	{
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		
		DescribeInstancesResult describeInstancesResult = this.ec2.describeInstances(describeInstancesRequest);
		List<Reservation> reservationList = describeInstancesResult.getReservations();
		List<Instance> instanceList = new ArrayList<Instance>();
		
		for(Reservation r : reservationList)
		{
			instanceList.addAll(r.getInstances());
		}
		return instanceList;
	}
	
	/*
	 * This function will create a new EC2 security group.
	 * 
	 * @param	groupName			The security group name.
	 * @param	groupDesc			The security group description.
	 * @param	allowedIPRanges		This is the list of IP Ranges that may connect to a EC2 Instance using this security group.
	 * @param	allowedTCPPorts		The EC2 Instance can only use these TCP Ports.
	 * @param	allowedUDPPorts		The EC2 Instance can only use these UDP Ports.
	 * @return	String				The groupId
	 */
	public String createSecurityGroup(String groupName, String groupDesc, String[] allowedIPRanges,  int[] allowedTCPPorts, int[] allowedUDPPorts) throws Exception
	{
        //ADD SECURITY GROUP
        String groupId = this.securityGroup.createSecurityGroup(groupName, groupDesc);
        if(groupId.equals(""))
        {
        	throw new Exception("createSecurityGroup Error: " + this.securityGroup.getErrorInformation().getErrorMessage());
        }
        
        //ASSIGN PORTS
        boolean portAssignStatus = this.securityGroup.assignPortsToSecurityGroup(groupName, allowedIPRanges, allowedTCPPorts, allowedUDPPorts);
        if(!portAssignStatus)
        {
        	throw new Exception("createSecurityGroup Error: " + this.securityGroup.getErrorInformation().getErrorMessage());
        }
        
        return groupId;
	}
	
	public void deleteSecurityGroup(String groupName) throws Exception
	{
		DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest();
		request.setGroupName(groupName);
	
		try
		{
			this.ec2.deleteSecurityGroup(request);
		}
		catch(AmazonServiceException e)
		{
			throw new Exception("deleteSecurityGroup Exception: " + e.getMessage());
		}
	}
	
	
}
