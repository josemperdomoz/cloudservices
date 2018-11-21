package aws;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class Awstests
{

    public static AmazonS3 s3;
    public static String filePath = "";
    public static String bucketName;
    public static String keyName;
    public static String[] keyNames;

    // You can specify the region with the appropriate credentials by
    // instatiating a AWS Service client without explicitly providing credentials to the builder!
    // Check this symbol ***

    public static void main(String[] args)
    {
        CSV measurements = new CSV();
        bucketName = "malihajose";
        keyName = "snow.png";
        filePath = "C:\\Users\\Usuario\\Desktop\\Bash\\JavaWebServices\\lab2_cs\\snow.png";
        keyNames = new String[]{"BoulderSaimaa2.jpg","BoulderSaimaa22.jpg"};
        String regionString = "us-west-2";
        String originBucket="jose-test-lab2";
        String destinationBucket="jose-test-lab1";



        // Regions to connect
        // us-west-2 US West (Oregon)
        // ap-northeast-2 Asia Pacific (Seoul)
        // eu-central-1 EU (Frankfurt)
        for(int i=0; i <30;i++)
        {
            try
            {
                s3 = AmazonS3ClientBuilder.standard()
                        .withRegion(regionString) // ***
                        .build();
                Date d = new Date();
                long t1 = d.getTime();

                downloadObject();

                d = new Date();
                long t2 = d.getTime();
                s3.getRegion();
                Long responseTime = t2 - t1;
                measurements.addToCSV(i,responseTime);
                System.out.println("The response time for measurement " + i + " uploading was: " + responseTime);
            }
            catch(AmazonServiceException e)
            {
                System.out.println(e.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public static void showBuckets()
    {
        List<Bucket> buckets = s3.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets)
        {
            System.out.println("* " + b.getName());
        }
    }

    public static void createBucket()
    {
        Bucket b =null;
        b = s3.createBucket(bucketName);
        System.out.println("Bucket created!! Name: " + b.getName() );
    }


    public static void deleteBucket()
    {

        s3.deleteBucket(bucketName);
        System.out.println("Bucket has been deleted!" );
    }


    public static void showBucketContent()
    {
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os: objects)
        {
            System.out.println("* " + os.getKey());
        }
    }


    public static void downloadObject()
    {
        try
        {
            S3Object s3obj = s3.getObject(bucketName, keyName);
            S3ObjectInputStream s3is = s3obj.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(keyName));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            // Writing file read from S3Object and writing it into a new File
            while ((read_len = s3is.read(read_buf)) > 0)
            {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        }
        catch(FileNotFoundException e1)
        {
            System.out.println(e1.toString());
        }
        catch(IOException e2)
        {
            System.out.println(e2.toString());
        }
    }


    public static void copyObject(String originBucket, String destinationBucket)
    {
        try
        {
            s3.copyObject(originBucket, keyName, destinationBucket, keyName);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    public static void deleteObject()
    {
        s3.deleteObject(bucketName, keyName);
    }


    public static void deleteMultipleObjects()
    {
        try
        {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName).withKeys(keyNames);
            s3.deleteObjects(dor);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }


    public static void uploadObject()
    {
        try
        {
            s3.putObject(bucketName, keyName, new File(filePath));
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

}
