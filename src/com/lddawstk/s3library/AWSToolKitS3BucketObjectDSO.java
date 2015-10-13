package com.lddawstk.s3library;

public class AWSToolKitS3BucketObjectDSO
{
	public String name;
	public long size;
	public boolean isFolder;
	
	public AWSToolKitS3BucketObjectDSO()
	{
		this.name = "";
		this.size = 0L;
		this.isFolder = false;
	}
}
