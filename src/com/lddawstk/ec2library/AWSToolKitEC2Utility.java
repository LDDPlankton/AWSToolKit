package com.lddawstk.ec2library;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.ServiceAbbreviations;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.SpotPrice;

public class AWSToolKitEC2Utility
{
	private AmazonEC2Client ec2;
	
	public AWSToolKitEC2Utility()
	{
		this.ec2 = AmazonEC2ClientWrapper.getInstance();
	}
	
	/*
	 * This function will list all Availability Zones that you have access to, based on your specified Region.
	 */
	public void listAvailabilityZones()
	{
        DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
        List<AvailabilityZone> listZones = availabilityZonesResult.getAvailabilityZones();
        for(AvailabilityZone i : listZones)
        {
        	String detail = String.format("Region Name: %s | Region State: %s | Avail Zone Name: %s", i.getRegionName(), i.getState(), i.getZoneName());
        	System.out.println( detail );
        }
	}
	
	/*
	 * This function will return a list of regions.
	 * 
	 * Region.getName() => is-gov-west-1, us-east-1, us-west-2, etc
	 * 
	 * @return List<Regions>
	 */
	public List<Regions> getRegionList()
	{
        List<Regions> regionList = new ArrayList<Regions>(EnumSet.allOf(Regions.class));
        return regionList;
	}
	
	/*
	 * This function will return a list of the various Instance Types we can use.
	 * 
	 * r.name() = T1Micro, M1Small, etc
	 * 
	 * @return List<InstanceType>
	 */
	public List<InstanceType> getInstanceList()
	{
		List<InstanceType> instanceList = new ArrayList<InstanceType>(EnumSet.allOf(InstanceType.class));
		//List<String> instanceList2 = new ArrayList<String>(EnumSet.allOf(InstanceType.class));
		return instanceList;
	}
	
	public void test()
	{
		//PRINT REGION INFORMATION [ONLY WORKS RUNNING ON AN EC2 INSTANCE]
        Region region = Regions.getCurrentRegion();
        if(region != null)	
        {
        	System.out.println("Region Name: " + region.getName() );
        
	        Field[] ServiceAbbreviationFields = ServiceAbbreviations.class.getFields();
	        for(Field i : ServiceAbbreviationFields)
	        {
	        	boolean supported = Region.getRegion(Regions.US_WEST_2).isServiceSupported( i.getName().toLowerCase() );
	        	System.out.println( String.format("Service Name: %s -> Supported: %b", i.getName(), supported) );
	        }
        }

	}
	
	public List<EC2AMIDSO> getAMIList()
	{
        /* GET ALL IMAGES       
		Ubuntu Server 14.04 LTS (HVM), SSD Volume Type - ami-5189a661
		Microsoft Windows Server 2012 R2 Base - ami-c3b3b1f3
		CentOS 7 (x86_64) with Updates HVM [US West (Oregon) 	ami-c7d092f7 || US East (N. Virginia) 	ami-96a818fe]
		Amazon Linux AMI 2015.03 (HVM), SSD Volume Type - ami-e7527ed7
			The Amazon Linux AMI is an EBS-backed, AWS-supported image. 
			The default image includes AWS command line tools, Python, Ruby, Perl, and Java. 
			The repositories include Docker, PHP, MySQL, PostgreSQL, and other packages.
         */
		List<EC2AMIDSO> amiList = new ArrayList<EC2AMIDSO>();
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.withOwners("amazon", "aws-marketplace");	//amazon, aws-marketplace
        List<Image> imgs = ec2.describeImages().getImages();
        
        for(Image i : imgs)
        {
        	if(i.getName() != null)
        	{
        		EC2AMIDSO model = new EC2AMIDSO();
        		model.name = i.getName();
        		model.description = i.getDescription();
        		model.architecture = i.getArchitecture();
        		model.imageId = i.getImageId();
        		model.imageOwnerAlias = i.getImageOwnerAlias();
        		
        		amiList.add(model);
        	}
        }
        return amiList;
        
        /*
        DescribeImagesRequest imagesRequest = new DescribeImagesRequest();
        List<String> owners = new ArrayList<String>();
        owners.add("AmiSavOwnerId");
        owners.add("AmiGclOwnerId");
        owners.add("NewAmiOwnerId");
        imagesRequest.setOwners(owners);
        
        Filter availabilityFilter = new Filter();
        availabilityFilter.setName("state");
        List<String> filterValues = new ArrayList<String>();
        filterValues.add("available");
        availabilityFilter.setValues(filterValues);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(availabilityFilter);
        imagesRequest.setFilters(filters);
 
        DescribeImagesResult imagesResponse = AmazonEC2ClientWrapper.getInstance().describeImages(imagesRequest);
        List<Image> images = imagesResponse.getImages();
        for(Image i : images)
        {
        	System.out.println(i.getImageId() + "||" + i.getDescription() + "||" + i.getOwnerId() );
        }
        */
	}
	
	/*
	 * This function will return a list of Spot Price History.
	 * 
	 * This function assists with getting the current Spot Price information, so that we can bid on Elastic Compute Cloud capacity, which requires our bid to be met.
	 * More Info: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/tutorial-spot-instances-java.html
	 */
	public void getSpotPriceHistory()
	{
		//PREV 30 DAYS
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -30);
		Date todayDateMinus30Days = cal.getTime();
		
		//TODAY
		Date today = new Date();
		
		//BUILD SPOT PRICE HISTORY REQUEST
        DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest();
        //request.withInstanceTypes("t1.micro");
        //request.withAvailabilityZone(defaultRegion);
        request.withStartTime(todayDateMinus30Days);
        request.withEndTime(today);
        DescribeSpotPriceHistoryResult describeSPHResult = ec2.describeSpotPriceHistory(request);
        
        List<SpotPrice> spotPrice = describeSPHResult.getSpotPriceHistory();
        List<EC2SpotHistoryPriceDSO> listOfPricingHistory = new ArrayList<EC2SpotHistoryPriceDSO>();
        for(SpotPrice i : spotPrice)
        {
        	//DETERMINE IF THIS SPOT PRICE IS ALREADLY IN OUR LIST [OTHERWISE IT WILL FILL UP WITH PREVIOUS LISTINGS AND WE WANT THE CURRENT DATA]
        	boolean foundHistory = false;
        	for(EC2SpotHistoryPriceDSO j : listOfPricingHistory)
        	{        		
        		if(i.getInstanceType().equals(j.ec2InstanceType) && i.getProductDescription().equals(j.ec2ProductDescription))
        			foundHistory = true;
        	}
        	//IF NO HISTORY OF THIS INSTANCE TYPE [t1.micro, etc] && PRODUCT TYPE [Windows, etc] ADD IT
        	if(!foundHistory)
        	{
				EC2SpotHistoryPriceDSO obj = new EC2SpotHistoryPriceDSO();
				obj.ec2InstanceType = i.getInstanceType();
				obj.ec2ProductDescription = i.getProductDescription();
				obj.ec2SpotPrice = i.getSpotPrice();
				obj.ec2AvailabilityZone = i.getAvailabilityZone();
				obj.ec2TimeStamp = i.getTimestamp();
				listOfPricingHistory.add(obj);
				
				System.out.println(i.getInstanceType() + " || " + i.getProductDescription() + " || " + i.getSpotPrice() + " || " + i.getAvailabilityZone() + " || " + i.getTimestamp().toString() );
        	}
        }
	}
}
