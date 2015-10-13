package com.lddawstk.main;

import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.AllowedMethods;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.Headers;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.PriceClass;
import com.amazonaws.services.cloudfront.model.TrustedSigners;
import com.amazonaws.services.cloudfront.model.ViewerCertificate;
import com.amazonaws.services.cloudfront.model.ViewerProtocolPolicy;
import com.lddawstk.cloudfrontlibrary.AmazonCloudFrontClientWrapper;

public class AWSToolKitCloudFrontUsageExample
{
	private AmazonCloudFrontClient cf = null;
	
	public AWSToolKitCloudFrontUsageExample()
	{
		this.cf = AmazonCloudFrontClientWrapper.getInstance();
	}
	
	public void test()
	{
		CreateDistributionRequest createDistributionRequest = new CreateDistributionRequest();
		
		//ORIGIN SETTINGS
		Origin origin = new Origin();
		Origins origins = new Origins();
		origin.withDomainName("bucketName" + "s3.amazonaws.com");							//Bucket Name + s3 Base Path
		origin.withOriginPath("");															//Optional. No '/' at end. used to point to a bucket/path location.
		origin.withId("my-id1");															//Unique ID For Reference
		origins.withItems(origin);
		origins.withQuantity(1);															//
		
		//DEFAULT CACHE BEHAVIOR
		DefaultCacheBehavior defaultCacheBehavior = new DefaultCacheBehavior();
		ForwardedValues forwardedValues = new ForwardedValues();
		Headers headers = new Headers();
		CookiePreference cookies = new CookiePreference();
		AllowedMethods methods = new AllowedMethods();
		TrustedSigners trustedSigners = new TrustedSigners();
		trustedSigners.withEnabled(true);													//
		trustedSigners.withQuantity(0);
		
		long minTTL = 0;
		long maxTTL = 31536000;
		methods.withItems("GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE");
		methods.withQuantity(7);
		headers.withItems("none");															//none,whitelist,all
		headers.withQuantity(1);
		cookies.withForward("none");														//none,whitelist,all
		
		forwardedValues.withHeaders(headers);
		forwardedValues.withCookies(cookies);												//Determine if we want to pass cookies in request url to origin.
		forwardedValues.withQueryString(true);												//True=Will forward query strings in request urls. Useful if different values returned based on query str.
		
		defaultCacheBehavior.withViewerProtocolPolicy(ViewerProtocolPolicy.AllowAll);		//Determines request allowed request type [http, https, both, redirect http->s
		defaultCacheBehavior.withAllowedMethods(methods);									//Methods: GET, HEAD, OPTIONS, PUT, POST, PATCH, DELETE

		defaultCacheBehavior.withMinTTL(minTTL);											//Time to stay in cache before we check object for update again.
		defaultCacheBehavior.withMaxTTL(maxTTL);
		defaultCacheBehavior.withForwardedValues(forwardedValues);
		defaultCacheBehavior.withSmoothStreaming(false);									//If using Microsoft Smooth Streaming for on-demand streaming
		defaultCacheBehavior.withTargetOriginId("some-target-origin-id");
		defaultCacheBehavior.withTrustedSigners(trustedSigners);
		

		
		//DISTRIBUTION SETTINGS		
		DistributionConfig distributionConfig = new DistributionConfig();
		ViewerCertificate viewerCertificate = new ViewerCertificate();
		viewerCertificate.withCloudFrontDefaultCertificate(true);							//True=Default CloudFront Certificate (*.cloudfront.net)
																							//False=Custom SSL Certificate (stored in AWS IAM)		
		
		LoggingConfig logging = new LoggingConfig();
		logging.withEnabled(true);															//True=Log all viewer requests, costs money. Requires, bucket + cookie selection if enabled.
		logging.withBucket("bucketName");													//Optional. Bucket for logs.
		logging.withPrefix("cookie-log");													//Optional. For simplification of browsing log files.
		logging.withIncludeCookies(false);													//Optional. Include cookies in access logs
		
		distributionConfig.withPriceClass(PriceClass.PriceClass_100);						//PriceClass_100=US+Europe
		distributionConfig.withViewerCertificate(viewerCertificate);
		distributionConfig.withDefaultRootObject("index.html");								//SERVED WHEN REQUESTING ROOT URL
		distributionConfig.withLogging(logging);
		distributionConfig.withComment("my-comment");
		distributionConfig.withCallerReference("some-caller-reference");					// ???
		distributionConfig.withEnabled(true);												//ACTIVE STATUS
		distributionConfig.withDefaultCacheBehavior(defaultCacheBehavior);
		distributionConfig.withOrigins(origins);
		
		//CREATE DISTRIBUTION
		createDistributionRequest.withDistributionConfig(distributionConfig);
		this.cf.createDistribution(createDistributionRequest);

		

	}
	
	
	
}
