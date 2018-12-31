
package jcloud;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
//import org.jclouds.aws.
import com.amazonaws.util.IOUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.apache.commons.codec.Charsets;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.sshj.config.SshjSshClientModule;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;


public class jcloud_tests {

    public static void main(String[] args)
    {
        // Include credentials as Strings here

        String filePath = "C:\\Users\\Usuario\\Desktop\\Bash\\JavaWebServices\\lab2_cs";
        String containerName = "jose-test-lab2-4";


        try
        {
            BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
                    .credentials(accesskeyid, secretkey)
                    .buildView(BlobStoreContext.class);

            BlobStore blobStore = context.getBlobStore();


            Location location = Iterables.tryFind(blobStore.listAssignableLocations(),
                    LocationPredicates.idEquals(Region.EU_CENTRAL_1)).orNull();

            String imageName = "BoulderSaimaa22.jpg";


            ByteSource payload = Files.asByteSource(new File(imageName));
            Blob uploadBlob = blobStore.blobBuilder("BoulderPic.jpg")
                    .payload(payload)
                    .contentLength(payload.size())
                    .build();

            //blobStore.createContainerInLocation(location,"jose-test-lab2-4");



            blobStore.removeBlob(containerName, "C:\\Users\\Usuario\\Desktop\\Bash\\JavaWebServices\\lab2_cs\\snow.png");
            listBlobsInContainer(containerName, blobStore);
            // Uploading Object
            //blobStore.putBlob(containerName, blob);

            //System.out.println("File successfully uploaded! ");


            Blob downloadBlob = blobStore.getBlob(containerName,"BoulderPic.jpg");

            uploadBlob(containerName, blobStore, uploadBlob);
            downloadBlob(downloadBlob);
            deleteBlob(containerName,"C:\\Users\\Usuario\\Desktop\\Bash\\JavaWebServices\\lab2_cs\\snow.png", blobStore);
            listBlobsInContainer(containerName,blobStore);

            context.close();
            /*



            System.out.println(location);
            System.out.println(" Bucket was created in " + location.getId());

            */
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }
    // get a context with ec2 that offers the portable ComputeService API


    public static void downloadBlob(Blob blob) throws IOException
    {

        InputStream is = blob.getPayload().getInput();
        File downloadFile = new File("BoulderPic.jpg");
        OutputStream outputStream = new FileOutputStream(downloadFile);

        byte[] buffer = new byte[8*1024];
        int bytesRead;

        while((bytesRead = is.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }

        is.close();
        outputStream.close();
        System.out.println("File Downloaded Successfully!");
    }


    public static void uploadBlob(String containerName, BlobStore blobStore, Blob blob)
    {
        blobStore.putBlob(containerName ,blob);
        System.out.println("File Uploaded Successfully!");
    }

    public static void listContainers(BlobStore blobStore) throws IOException
    {
        for (int i = 0; i < blobStore.list().size(); i++)
        {
                System.out.println(blobStore.list().toArray()[i]);
        }
    }
    public static void listLocations (BlobStore blobStore)
    {
            for (int i = 0; i < blobStore.listAssignableLocations().size(); i++)
            {
                System.out.println(blobStore.listAssignableLocations().toArray()[i]);
            }

    }


    public static void listBlobsInContainer(String containerName, BlobStore blobStore)
    {
        for(int i=0; i<blobStore.list(containerName).size();i++)
        {
            System.out.println(blobStore.list(containerName).toArray()[i]);
        }
    }

    public static void deleteBlob(String containerName, String blobName, BlobStore blobStore)
    {
        blobStore.removeBlob(containerName,blobName);
        System.out.println("File Deleted Successfully!");
    }


}
