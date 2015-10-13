package com.lddawstk.main;

import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.lddawstk.ec2library.AWSToolKitEC2Manager;
import com.lddawstk.ec2library.AWSToolKitEC2SecurityGroup;
import com.lddawstk.ec2library.AWSToolKitEC2SecurityGroupUtil;
import com.lddawstk.ec2library.AWSToolKitEC2Utility;
import com.lddawstk.ec2library.AmazonEC2ClientWrapper;
import com.lddawstk.ec2library.EC2AMIDSO;
import com.lddawstk.ec2library.EC2SecurityGroupDSO;

public class AWSToolKitEC2UsageExample
{
	private AWSToolKitEC2Manager manager;
	
	public AWSToolKitEC2UsageExample()
	{
		this.manager = new AWSToolKitEC2Manager();
	}
	
	public void printInstanceList()
	{
		List<String> retList = this.manager.getInstanceList();
		System.out.println("AWS EC2 Instance Types: ");
		
		for(String i : retList)
		{
			System.out.println(i + ",");
		}
	}
	
	public void printAMIList()
	{
		List<EC2AMIDSO> amiList = this.manager.getAMIList();
		
		System.out.println("Now Listing all AMI's");
		for(EC2AMIDSO model : amiList)
		{
			String line = String.format("Name: %s | Desc: %s | Architecture: %s | ImageId: %s | ImageOwnerAlias: %s", 
					model.name, model.description, model.architecture, model.imageId, model.imageOwnerAlias);
			System.out.println(line);
		}
	}
	
	public void printSecurityGroups()
	{
		List<EC2SecurityGroupDSO> secGroupList = this.manager.getSecurityGroupList();
		
		for(EC2SecurityGroupDSO model : secGroupList)
		{
        	String line = String.format("GroupId: %s | GroupName: %s | Desc: %s | VpcId: %s | Tags: %s", 
        			model.groupId, model.groupName, model.description, model.vpcId, model.tags);
        	System.out.println(line);
		}
	}
	
	public boolean createKeyPair()
	{
		try
		{
			String privateKeyPEMEncoded = this.manager.createKeyPair("MY_KEY_NAME");
			
			//PRINT OR DO SOMETHING HERE WITH OUR PRIVATE KEY [PERHAPS SHARE THROUGH SOCIAL MEDIA :) ?]
			
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean deleteKeyPair()
	{	
		try
		{
			boolean status = this.manager.deleteKeyPair("MY_KEY_NAME");
			
			return status;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean createEC2Instance()
	{
		try
		{
			String instanceType = "t2.micro";
			String keyName = "MY_KEY_NAME";
			String imageId = "ami-0d4cfd66";
			int launchMinInstances = 1;
			int launchMaxInstances = 1;
			List<Instance> instanceList = this.manager.createEC2Instance(instanceType, keyName, imageId, launchMinInstances, launchMaxInstances);
			
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean deleteEC2Instance()
	{
		try
		{
			String instanceId = "SOME_INSTANCE_ID";
			this.manager.deleteEC2Instance(instanceId);
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public void createSecurityGroup()
	{
        String[] allowedIPRanges = new String[] { AWSToolKitEC2SecurityGroupUtil.generateLocalIPRange() };
        int[] allowedTCPPorts = new int[] {20,21,22,25,53,80,110,143,443,465,587,993,995};
        int[] allowedUDPPorts = new int[] {20,21,53};
        String groupName = "GROUP_NAME1_API";
        String groupDesc = "SOME DESC API";
        
        try
        {
	        String groupId = this.manager.createSecurityGroup(groupName, groupDesc, allowedIPRanges, allowedTCPPorts, allowedUDPPorts);
	        System.out.println("Added Security Group: Id=" + groupId);
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        }
	}
	
	public void deleteSecurityGroup()
	{
		String securityGroupName = "GROUP_NAME1_API";
		
        try
        {
        	this.manager.deleteSecurityGroup(securityGroupName);
        	System.out.println("Security Group Deleted!");
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        }
		
	}
}
