package SenseLab.SensorTest;

import com.example.quade.senselab.SensorTag;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jacob on 2/24/2016.
 * This test is created to test the sensor tag object for errors.
 */
public class SensorTest {

    //this test will make sure the creation of a sensorTag is working properly
    @Test
    public void testSensorCreation(){

        //create a test sensor by calling default constructor
        SensorTag testSensor = new SensorTag();

        //make sure the sensor is empty after the default construction
        assertTrue(testSensor.isEmpty());

        //make sure the sensor list of data is equal to null
        assertTrue(testSensor.dataList.equals(null));

        System.out.print("The sensor Creation tested correctly");
    }

    //This tests the ability to add some data to the sensor
    @Test
    public void testSensorAddData(){

        //create a dummy sensor by calling the default constructor
        SensorTag testSensor = new SensorTag();

        //create a dummy piece of data to add to the sensor's data list
        //NOTE: Until the data class is more fully developed this is being tested with a string object
        String dummyData = new String("This is the dummy data string");

        //add the data to the sensor
        testSensor.addData(dummyData);

        //check that the sensor is no longer empty
        assertFalse(testSensor.isEmpty());

        System.out.print("Some data was successfully added to the sensor");

    }
}
