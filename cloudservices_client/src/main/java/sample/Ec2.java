package sample;

import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticache.model.CreateCacheSecurityGroupRequest;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.opsworks.model.StartInstanceRequest;
import com.amazonaws.services.s3.model.Bucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;


public class Ec2 {
    public AmazonEC2 ec2;
    public ArrayList<InstanceEntry> instancelist;
    public String[] regions = {"eu-central-1", "eu-west-1", "us-west-2", "sa-east-1", "eu-west-2"}; // us-west-2
    public String[] amis = {"ami-7c1bfd1b", "ami-0b0a60c0a2bd40612", "ami-0ca1b0ae0ac576ff9"}; // This AMIs correspond to Oregon Availability Zone


    public static void main(String[] args) {
        String groupName = "";
        String groupDesc = "";
        String vpc_id = "";
        //String region="us-west-2"; //ireland
        String keyName = "";
        String amiID = "";
        String instanceID = "";

        //describeInstance();
        //stopInstance(instanceID);
        //listKeyPair(Ec2);
        //createKeyPair(keyName,Ec2);
        //createInstance(ami_id, keyName, groupName, region,ec3);
        //authorizeSecurityGroup(groupName,Ec2);
        //describeSecurityGroup(groupName,Ec2);
        //listRegion(ec2);
        //createSecurityGroup(groupName,groupDesc,vpc_id,region,Ec2);
        //deleteSecurityGroup(groupName,region,Ec2);
    }

    public ArrayList<InstanceEntry> getInstancesInfo(int choice) {
        //choice = GUIist.getSelectionModel().getSelectedIndex();
        String region = regions[choice];
        boolean done = false;
        ec2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        instancelist = new ArrayList<>();

        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    String instanceIdentifier = instance.getInstanceId();
                    String instanceName = instance.getKeyName();
                    String instanceType = instance.getInstanceType();
                    String instanceState = instance.getState().getName();
                    String instanceMonitoring = instance.getMonitoring().getState();

                    instancelist.add(new InstanceEntry(instanceIdentifier, instanceName, instanceType, instanceState, instanceMonitoring));
                }
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }


        }
        return instancelist;
    }


    public void createInstance(String amiID, String keyName, String groupName, int choice, int numberOfInstances) {
        String region = regions[choice];
        //create instance using AmazonEC2 method

         ec2 = AmazonEC2ClientBuilder
                .standard()
                .withRegion(region)
                .build();


        createKeyPair(keyName);
        createSecurityGroup(groupName, choice);
        authorizeSecurityGroup(groupName);


        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withImageId(amiID)
                .withInstanceType("t2.micro")
                .withKeyName(keyName)
                .withMinCount(1)
                .withMaxCount(numberOfInstances)
                .withSecurityGroups(groupName);

        System.out.println(runInstancesRequest);
        RunInstancesResult result = ec2.runInstances(runInstancesRequest);



        /*
        String yourInstanceId = ec2Client.runInstances(runInstancesRequest)
                .getReservation().getInstances().get(0).getInstanceId();
        System.out.println(yourInstanceId);

        Tag tag = Tag.builder()
                .key("maliJose")
                .value(keyName)
                .build();

        CreateTagsRequest tagReq= CreateTagsRequest.builder().tags(tag).build();
        */

    }


    public ArrayList<String> getCloudwatchMetricData(int regionChoice, int pointer) {
        //describeInstanceWithID(instanceID, region);
        ArrayList<String> result = new ArrayList<>();
        String region = regions[regionChoice];
        String instanceID = instancelist.get(pointer).getInstanceId();
        double metric = 0;
        Date timeStamp = null;
        String unit = null;
        String nameSpace = "AWS/EC2";
        String statistic = "Average";
        long offsetInMilliseconds = 1000 * 60 * 60;
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard().withRegion(region).build();
        String cpu = getCPUUtilization(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        String networkin = getNetworkInBytes(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        String networkout = getNetworkOutBytes(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        String diskin = getDiskReadInBytes(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        String networkpacketsin = getNetworkPktsIn(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        String networkpacketsout = getNetworkPktsOut(region, instanceID, metric, timeStamp, unit, nameSpace, statistic,offsetInMilliseconds, cw);
        result.add(cpu);
        result.add(networkin);
        result.add(networkout);
        result.add(diskin);
        result.add(networkpacketsin);
        result.add(networkpacketsout);
        return result;
    }

    public String getCPUUtilization(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();

        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("CPUUtilization")
                .withStatistics(statistic)
                .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();

        for (Object aDataPoint : dataPoint) {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp = dp.getTimestamp();
            metric = dp.getAverage();
            unit = dp.getUnit();
            sb.append(timeStamp + "\t" + metric + "\t%");
        }
        return sb.toString();
    }


    public String getNetworkOutBytes(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();
        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("NetworkOut")
                .withStatistics(statistic)
                .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for(Object aDataPoint : dataPoint)
        {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp=dp.getTimestamp();
            metric=dp.getAverage();
            unit=dp.getUnit();

            sb.append(timeStamp + "\t" + metric + " Outbound Bytes" );
        }
        return sb.toString();
    }



    public String getNetworkInBytes(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();
        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("NetworkIn")
                .withStatistics(statistic)
                .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for(Object aDataPoint : dataPoint)
        {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp=dp.getTimestamp();
            metric=dp.getAverage();
            unit=dp.getUnit();

            sb.append(timeStamp + "\t" + metric + "Inbound Bytes" );
        }
        return sb.toString();
    }

    public String getNetworkPktsIn(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();
        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("NetworkPacketsIn")
                .withStatistics(statistic)
                .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for(Object aDataPoint : dataPoint)
        {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp=dp.getTimestamp();
            metric=dp.getAverage();
            unit=dp.getUnit();

            sb.append(timeStamp + "\t" + metric + " Inbound Pkts" );
        }
        return sb.toString();
    }

    public String getNetworkPktsOut(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();
        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("NetworkPacketsOut")
                .withStatistics(statistic)
                .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for(Object aDataPoint : dataPoint)
        {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp=dp.getTimestamp();
            metric=dp.getAverage();
            unit=dp.getUnit();

            sb.append(timeStamp + "\t" + metric + "Outbound Pkts" );
        }
        return sb.toString();
    }

    public String getDiskReadInBytes(String region, String instanceID, double metric, Date timeStamp, String unit,String nameSpace, String statistic, long offsetInMilliseconds, AmazonCloudWatch cw)
    {
        StringBuilder sb = new StringBuilder();
        GetMetricStatisticsRequest request1 = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withNamespace(nameSpace)
                .withPeriod(60 * 60)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceID))
                .withMetricName("DiskReadBytes")
                .withStatistics(statistic)
                .withEndTime(new Date());


        GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request1);
        List dataPoint = getMetricStatisticsResult.getDatapoints();
        for(Object aDataPoint : dataPoint)
        {
            Datapoint dp = (Datapoint) aDataPoint;
            timeStamp=dp.getTimestamp();
            metric=dp.getAverage();
            unit=dp.getUnit();
            sb.append(timeStamp + "\t" + metric + "\tBytes");
        }
        return sb.toString();
    }







    public void stopInstance(int regionChoice, int pointer)
    {
        String region = regions[regionChoice];
        String instance_id = instancelist.get(pointer).getInstanceId();
        ec2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();
        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);
    }

    public void startInstance(int regionChoice, int pointer)
    {
        String region = regions[regionChoice];
        String instance_id = instancelist.get(pointer).getInstanceId();
        ec2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);
        ec2.startInstances(request);
    }

    public ArrayList<String> listRegion()
    {
        ArrayList<String> regionsEndpoints = new ArrayList();
        String regionEndpoint;
        ec2 = AmazonEC2ClientBuilder.defaultClient();
        DescribeRegionsResult regions_response = ec2.describeRegions();
        for(Region region : regions_response.getRegions()) {
            regionEndpoint = region.getRegionName() + " with endpoint " + region.getEndpoint();
            regionsEndpoints.add(regionEndpoint);
        }
        return regionsEndpoints;
    }



    public  void createKeyPair(String keyName)

    {
        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        createKeyPairRequest.withKeyName(keyName);
        CreateKeyPairResult createKeyPairResult =
                ec2.createKeyPair(createKeyPairRequest);
        KeyPair keyPair = new KeyPair();
        keyPair = createKeyPairResult.getKeyPair();
    }


    public void createSecurityGroup(String groupName, int choice)

    {
        String region = regions[choice];
        String groupDesc = "Lab3_JoseMali";
        String vpc_id="";
        //final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();


        //check if the security group already exists or not

        String groupID;
        CreateSecurityGroupRequest create_request = new CreateSecurityGroupRequest()
                .withGroupName(groupName)
                .withDescription(groupDesc)
                .withVpcId(vpc_id);

        CreateSecurityGroupResult create_response = ec2.createSecurityGroup(create_request);
        System.out.println(create_response.toString());
        System.out.println("Succeessfully created security group name: " + groupName);
        groupID=create_response.getGroupId();
        System.out.println("Security group ID is: " + groupID);
    }


    public void authorizeSecurityGroup(String groupName)
    {
        //line 98-111 : setting user defined IP ranges to security groups
        /*IpPermission ipPermission = new IpPermission();
        IpRange ipRange1 = new IpRange().withCidrIp("0.0.0.0/32");
        IpRange ipRange2 = new IpRange().withCidrIp("100.10.10.10/32");
        ipPermission.withIpv4Ranges(Arrays.asList(new IpRange[] {ipRange1, ipRange2}))
                .withIpProtocol("tcp")
                .withFromPort(22)
                .withToPort(22);

        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
                new AuthorizeSecurityGroupIngressRequest();
        authorizeSecurityGroupIngressRequest.withGroupName(groupName)
                .withIpPermissions(ipPermission);


        ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);*/

        //assigning access to anywhere
        IpRange ip_range = new IpRange()
                .withCidrIp("0.0.0.0/0");

        IpPermission ip_perm = new IpPermission()
                .withIpProtocol("tcp")
                .withToPort(80)
                .withFromPort(80)
                .withIpv4Ranges(ip_range);

        IpPermission ip_perm2 = new IpPermission()
                .withIpProtocol("tcp")
                .withToPort(22)
                .withFromPort(22)
                .withIpv4Ranges(ip_range);

        AuthorizeSecurityGroupIngressRequest auth_request = new
                AuthorizeSecurityGroupIngressRequest()
                .withGroupName(groupName)
                .withIpPermissions(ip_perm, ip_perm2);

        AuthorizeSecurityGroupIngressResult auth_response =
                ec2.authorizeSecurityGroupIngress(auth_request);
    }

}