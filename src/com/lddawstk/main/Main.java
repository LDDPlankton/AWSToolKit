package com.lddawstk.main;

import java.util.List;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.lddawstk.cloudfrontlibrary.AmazonCloudFrontClientWrapper;
import com.lddawstk.ec2library.AWSToolKitEC2Manager;
import com.lddawstk.ec2library.AmazonEC2ClientWrapper;
import com.lddawstk.ec2library.EC2AMIDSO;
import com.lddawstk.ec2library.EC2SecurityGroupDSO;
import com.lddawstk.s3library.AWSToolKitS3UsageExample;
import com.lddawstk.s3library.AmazonS3ClientWrapper;

public class Main
{
	private static final String VERSION = "1.0.0";
	
	public static void printMenu()
	{		
		String[] options = new String[]
				{
						"createKeyPair <KEY_PAIR_NAME>",
						"deleteKeyPair <KEY_PAIR_NAME>",
						"printInstanceTypeList",
						"printAMIList",
						"createSecurityGroup <GROUP_NAME> <GROUP_DESC> <ALLOWED IP RANGE>, <ALLOWED TCP PORTS> <ALLOWED UDP PORTS>",
						"deleteSecurityGroup <GROUP_NAME>",
						"printSecurityGroupList",
						"createEC2Instance <INSTANCE_TYPE> <KEYPAIR_NAME> <AMI ID> <MIN_NUM_INSTANCES_TO_LAUNCH> <MAX_INSTANCES_TO_LAUNCH>",
						"deleteEC2Instance <INSTANCE_ID>",
						"printEC2InstanceList"
				};

		String[] descriptions = new String[]
				{
						"Create new Key Pair",
						"Delete Key Pair",
						"Prints Instance Types: T1Micro, M1Small, ...",
						"Prints List of All Amazon Machine Images (A Long List).",
						"Add new Security Group. Example Usage: GroupName GroupDesc 10.10.10.5/24,1.2.3.4/24 22,25,53,80,443 21,53",
						"Delete Security Group",
						"Show List of all Security Groups",
						"Create new EC2 Instance.",
						"Delete EC2 Instance.",
						"Show List of all EC2 Instances."
				};
		
		System.out.println("AWSToolKit " + VERSION + "\n"
				+ "Usage: java -jar <program> <option> <args...>\n");
		for(int i = 0; i < options.length; i++)
			System.out.println(String.format("%-59s: %s", options[i], descriptions[i]));				
	}
	
	public static void requireNumberOfArguments(String args[], int required)
	{
		if(args.length != required)
		{
			System.out.println("Invalid Number of Arguments!");
			System.exit(-1);
		}
	}
	
	public static void init()
	{
		AWSAuthentication auth = new AWSAuthentication();
		boolean authenticationStatus = auth.processAuthentication(null, null);
		if(!authenticationStatus)
		{
			System.out.println( auth.getErrorInformation().getErrorMessage() );
			System.exit(1);
		}
		BasicSessionCredentials credentials = auth.getAuthCredentials();
		
		//SET CREDENTIALS IN WRAPPERS
		AmazonEC2ClientWrapper.setInstance(credentials);						//SET AmazonEC2Client Info
		AmazonS3ClientWrapper.setInstance(credentials);
		AmazonCloudFrontClientWrapper.setInstance(credentials);
		
		//AmazonIdentityManagementClient iam=new AmazonIdentityManagementClient(credentials);
	}
	
	public static void main(String[] args)
	{
		init();
		AWSToolKitEC2Manager ec2m = new AWSToolKitEC2Manager();
		
		//ENSURE ARGUMENTS PASSED
		if(args.length < 1)
		{
			printMenu();
			System.exit(-1);
		}	
		
		switch(args[0])
		{
			case "createKeyPair":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 2);

				//ASSIGN VARIABLES
				String key_pair_name1 = args[1];
				
				try 
				{
					String privateKeyPEMEncoded1 = ec2m.createKeyPair(key_pair_name1);
					System.out.println(String.format("Private Key PEM Encoded=[%s]", privateKeyPEMEncoded1));
				} 
				catch (Exception e)
				{
	
				}
			break;
			
			case "deleteKeyPair":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 2);

				//ASSIGN VARIABLES
				String key_pair_name2 = args[1];
				
				try 
				{
					ec2m.deleteKeyPair(key_pair_name2);
				} 
				catch (Exception e)
				{
	
				}
			break;
			
			case "printInstanceTypeList":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 1);
				
				List<String> instanceTypeList = ec2m.getInstanceList();
				for(String it : instanceTypeList)
					System.out.println(it);
				
			break;
			
			case "printAMIList":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 1);
				
				List<EC2AMIDSO> amiList = ec2m.getAMIList();
				for(EC2AMIDSO model : amiList)
				{
					System.out.println(String.format("Name: %s   Image ID: ", model.name, model.imageId) );
				}
			break;
			
			case "createSecurityGroup":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 6);

				//ASSIGN VARIABLES
				String group_name6 = args[1];
				String group_desc6 = args[2];
				String allowed_ip_range6 = args[3];
				String allowed_tcp_ports6 = args[4];
				String allowed_udp_ports6 = args[5];
				
				//SPLIT
				String[] ipRangeSplit = allowed_ip_range6.split(",");
				String[] tcpPortSplit = allowed_tcp_ports6.split("");
				String[] udpPortSplit = allowed_udp_ports6.split("");
				
				//DECLARE + BUILD
				String[] allowedIPRanges = new String[ipRangeSplit.length];
				int[] allowedTCPPorts = new int[tcpPortSplit.length];
				int[] allowedUDPPorts = new int[udpPortSplit.length];
				for(int i = 0; i < ipRangeSplit.length; i++)
					allowedIPRanges[i] = ipRangeSplit[i].trim();
				for(int i = 0; i < tcpPortSplit.length; i++)
					allowedTCPPorts[i] = Integer.valueOf(tcpPortSplit[i].trim());
				for(int i = 0; i < udpPortSplit.length; i++)
					allowedUDPPorts[i] = Integer.valueOf(udpPortSplit[i].trim());
				try
				{
					String groupID6 = ec2m.createSecurityGroup(group_name6, group_desc6, allowedIPRanges,  allowedTCPPorts, allowedUDPPorts);
					System.out.println(String.format("Goup ID: %s Created!", groupID6));
				}
				catch(Exception e)
				{
					
				}
						
			break;
			
			case "deleteSecurityGroup":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 2);

				//ASSIGN VARIABLES
				String group_name7 = args[1];
				
				try
				{
					ec2m.deleteSecurityGroup(group_name7);
				}
				catch(Exception e)
				{
					
				}
			break;
			
			case "printSecurityGroupList":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 1);
				
				List<EC2SecurityGroupDSO> secList = ec2m.getSecurityGroupList();
				for(EC2SecurityGroupDSO model : secList)
				{
					System.out.println(String.format("ID: %s  Name: %s   VpcID: %s", model.groupId, model.groupName, model.vpcId));
				}
			break;
			
			case "createEC2Instance":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 6);

				//ASSIGN VARIABLES
				String instance_type9 = args[1];
				String key_pair_name9 = args[2];
				String ami_id9 = args[3];
				int min_instance_num9 = Integer.valueOf(args[4]);
				int max_instance_num9 = Integer.valueOf(args[5]);
				
				try
				{
					ec2m.createEC2Instance(instance_type9, key_pair_name9, ami_id9, min_instance_num9, max_instance_num9);
				}
				catch(Exception e)
				{
					
				}

			break;
			
			case "deleteEC2Instance":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 2);

				//ASSIGN VARIABLES
				String instance_id10 = args[1];
				
				try
				{
					ec2m.deleteEC2Instance(instance_id10);
				}
				catch(Exception e)
				{
					
				}
			break;
			
			case "printEC2InstanceList":
				//HAVE REQUIRED ARGS OR DIE
				requireNumberOfArguments(args, 1);

				List<Instance> myInstanceList = ec2m.getEC2InstanceList();
				for(Instance model : myInstanceList)
				{
					System.out.println(String.format("ID: %s   IP: %s   State: %s", model.getInstanceId(), model.getPublicIpAddress(), model.getState()));
				}
				
			break;
			
			default:
				printMenu();
		}
		
		
		/*
		AWSToolKitCloudFrontUsageExample cfUsage = new AWSToolKitCloudFrontUsageExample();
		cfUsage.test();
		
        AWSToolKitEC2UsageExample ec2Usage = new AWSToolKitEC2UsageExample();
        ec2Usage.createKeyPair();
        ec2Usage.instanceList();
        ec2Usage.createSecurityGroup();
        ec2Usage.listSecurityGroups();
        ec2Usage.createKeyPair();
        ec2Usage.getAMIList();
        ec2Usage.createEC2Instance();

        AWSToolKitS3UsageExample s3Usage = new AWSToolKitS3UsageExample();
        s3Usage.createBucket();
        s3Usage.createFolder();
        s3Usage.upload();
        s3Usage.listObjectsInBucket();
        s3Usage.listObjectsInBucketInFolder();
        s3Usage.isFileExist();
        s3Usage.isFolderExist();
        s3Usage.deleteFolder(); 
        */
	}

}
