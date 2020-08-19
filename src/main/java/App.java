import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class App{


    public static void main(String[] args) throws Exception {

        String apiKey = "rzp_test_5O8sHqdw3Hs0to";
        String secretKey = "OYvXnZ8lqkPBu79qrDFg9mos";
        new PayForOrder(apiKey, secretKey);

    }
}