
package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    private ToggleGroup aaa;
    @FXML
    private MenuButton ec2RegionsMenu;
    @FXML
    private TextField keyPairField2;
    @FXML
    private TextField securityGroupField;
    @FXML
    private TextField numberOfInstanceField;
    @FXML
    private Button launchButton;
    @FXML
    private Text instanceInfoText;
    @FXML
    private Button statusOfInstanceButton;
    @FXML
    private Button stopInstanceButton;
    @FXML
    private ListView<InstanceEntry> GUIList;
    @FXML
    private CheckMenuItem frankfurt;
    @FXML
    private CheckMenuItem ireland;
    @FXML
    private CheckMenuItem oregon;
    @FXML
    private CheckMenuItem saopaulo;
    @FXML
    private CheckMenuItem london;
    @FXML
    private CheckMenuItem redhat;
    @FXML
    private CheckMenuItem ubuntuServer18;
    @FXML
    private CheckMenuItem oregon1;
    @FXML
    private TextField amiField;
    @FXML
    private Label cpuUtilizationTabText;
    @FXML
    private Label networkInTabText;
    @FXML
    private Label networkOutTabText;
    @FXML
    private Label diskInBytesTabText;
    @FXML
    private Button availableRegionsButton;
    @FXML
    private ListView<String> GUIList2;
    @FXML
    private Label networkPktOutTabText;
    @FXML
    private Label networkPktInTabText;







    public Ec2 ec2loader = new Ec2();
    public ArrayList<InstanceEntry> instanceList = new ArrayList();
    public ArrayList<String> regionsList = new ArrayList();
    public ObservableList<InstanceEntry> olistInstanceList = FXCollections.observableArrayList();
    public ObservableList<String> olistRegionsList = FXCollections.observableArrayList();
    public int choice;
    private boolean regionSelected = false;
    public int amiChoice;



    public void initialize(URL url, ResourceBundle rb) {

        olistInstanceList = FXCollections.observableArrayList(instanceList);
        GUIList.setItems(olistInstanceList);
        olistInstanceList.setAll(instanceList);


        // TODO
    }


    // Region Selection

    @FXML
    void frankfurtListener(ActionEvent event){
        if(frankfurt.isSelected())
        {
            choice =0;
            instanceInfoText.setText("Frankfurt Region is Selected");
        }
        else
            System.out.println("Frankfurt region is deselected");
    }

    @FXML
    void irelandListener(ActionEvent event)
    {
        if(ireland.isSelected())
        {
            choice =1;
            instanceInfoText.setText("Ireland Region is Selected");
        }
        else
            System.out.println("Ireland region is deselected");
    }

    @FXML
    void oregonListener(ActionEvent event)
    {
        if(oregon.isSelected())
        {
            choice =2;
            instanceInfoText.setText("Oregon Region is Selected");
        }
        else
            System.out.println("Oregon region is deselected");
    }


    @FXML
    void saopauloListener(ActionEvent event){
        if(saopaulo.isSelected())
        {
            choice =3;
            instanceInfoText.setText("Sao Paulo Region is Selected");
        }
        else
            System.out.println("Sao Paulo region is deselected");
    }

    @FXML
    void londonListener(ActionEvent event){
        if(london.isSelected())
        {
            choice =4;
            instanceInfoText.setText("London Region is Selected");
        }
        else
        System.out.println("London region is deselected");

    }

    @FXML
    void redhatListener(ActionEvent event)
    {
        if(redhat.isSelected())
        {
            amiChoice=0;
            instanceInfoText.setText("AMI Selected!");
            amiField.setText("ami-036affea69a1101c9");
        }
        else
            System.out.println("AMI Deselected!");
    }


    @FXML
    void ubuntuServer18Listener(ActionEvent event){
        if(ubuntuServer18.isSelected())
        {
            amiChoice =1;
            instanceInfoText.setText("AMI Selected!");
            amiField.setText("ami-0bbe6b35405ecebdb");
        }
        else
            System.out.println("AMI Deselected!");
    }

    @FXML
    void windowsServer2012R2Listener(ActionEvent event){
        if(oregon1.isSelected())
        {
            amiChoice=2;
            instanceInfoText.setText("AMI Selected");
            amiField.setText("ami-0eade913a026daf38");
        }
        else
            System.out.println("AMI Deslected!");

    }








    @FXML
    void launchButtonClicked(ActionEvent event) {
        int instanceQuantity = Integer.parseInt(this.numberOfInstanceField.getText());
        //reateInstance(String amiID, String keyName, String groupName, int choice, int numberOfInstances)
        System.out.println(amiField.getText());
        System.out.println(keyPairField2.getText());
        System.out.println(securityGroupField.getText());
        System.out.println(choice);
        System.out.println(instanceQuantity);
        ec2loader.createInstance(amiField.getText(), keyPairField2.getText(), securityGroupField.getText(), choice, instanceQuantity);
        System.out.println("Instance Launched Sucessfully!");
    }

    @FXML
    void statusOfInstanceButtonClicked(ActionEvent event) {
        instanceList = ec2loader.getInstancesInfo(choice);
        olistInstanceList = FXCollections.observableArrayList(instanceList);
        olistInstanceList.setAll(instanceList);
        GUIList.setItems(olistInstanceList);
        instanceInfoText.setText("Instances are shown in the specified region");
        System.out.println(ec2RegionsMenu.getItems().toString());
    }

    @FXML
    void stopInstanceButtonClicked(ActionEvent event) {
        int pointer = GUIList.getSelectionModel().getSelectedIndex();
        ec2loader.stopInstance(choice, pointer);
        System.out.println("Instance Stopped Successfully!");

    }

    @FXML
    void monitorButtonClicked(ActionEvent event)
    {
        int pointer = GUIList.getSelectionModel().getSelectedIndex();
        ArrayList<String> result = ec2loader.getCloudwatchMetricData(choice, pointer);
        System.out.println("Metric Data fetched successfully!");

        for(int i=0; i<result.size(); i++)
        {
            System.out.println(result.get(i));
        }

        cpuUtilizationTabText.setText(result.get(0));
        networkInTabText.setText(result.get(1));
        networkOutTabText.setText(result.get(2));
        diskInBytesTabText.setText(result.get(3));
        networkPktInTabText.setText(result.get(4));
        networkPktOutTabText.setText(result.get(5));


    }

    @FXML
    void startInstanceButtonClicked(ActionEvent event)
    {
        int pointer = GUIList.getSelectionModel().getSelectedIndex();
        ec2loader.startInstance(choice, pointer);
        System.out.println("Instance Started Successfully!");
    }

    @FXML
    void availableRegionsButtonClicked(ActionEvent event)
    {
        regionsList = ec2loader.listRegion();
        olistRegionsList = FXCollections.observableArrayList(regionsList);
        olistRegionsList.setAll(regionsList);
        GUIList2.setItems(olistRegionsList);
        instanceInfoText.setText("Showing specified regions!");
        System.out.println(ec2RegionsMenu.getItems().toString());
    }

}
