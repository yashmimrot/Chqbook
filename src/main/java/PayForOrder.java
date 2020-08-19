
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import io.dropwizard.views.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Path("/")
public class PayForOrder {

    private RazorpayClient client;
    private int amount = 500;

    private String apiKey;
    private String secretKey;

    public PayForOrder(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        try {
            this.client = new RazorpayClient(this.apiKey, this.secretKey);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }

    }
    @GET
    @Path("/order")
    @Produces(MediaType.TEXT_HTML)
    public View getPaymentForm() throws RazorpayException, SQLException {
        JSONObject options = new JSONObject();
        options.put("amount", amount);
        options.put("currency", "INR");
        options.put("receipt", "sample_receipt");
        options.put("payment_capture", 1);
        Order order = client.Orders.create(options);
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/chqbook",
                        "postgres", "    ");
        c.setAutoCommit(false);
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO orders (ID,AMOUNT,AMOUNT_PAID,CURRENCY,RECEIPT,STATUS) "
                + "VALUES ("+order.get("id")+" , "+order.get("amount")+","+order.get("amount_paid")+ "INR" + ","+order.get("receipt") + order.get("status") +");";
        stmt.executeUpdate(sql);
        return new PaymentPage(amount, (String) order.get("id"));


    }

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pay(MultivaluedMap<String, String> formParams) {
        String paymentId = formParams.getFirst("razorpay_payment_id");
        String razorpaySignature = formParams.getFirst("razorpay_signature");
        String orderId = formParams.getFirst("razorpay_order_id");
        JSONObject options = new JSONObject();

        if (StringUtils.isNotBlank(paymentId) && StringUtils.isNotBlank(razorpaySignature)
                && StringUtils.isNotBlank(orderId)) {
            try {
                options.put("razorpay_payment_id", paymentId);
                options.put("razorpay_order_id", orderId);
                options.put("razorpay_signature", razorpaySignature);
                boolean isEqual = Utils.verifyPaymentSignature(options, this.secretKey);

                Connection c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/chqbook",
                                "postgres", "    ");
                c.setAutoCommit(false);
                Statement stmt = c.createStatement();
                String sql = "INSERT INTO orders (PAYMENT_ID,AMOUNT,CURRENCY,ID) "
                        + "VALUES ("+paymentId+" , "+amount+", INR,"+ orderId+");";
                stmt.executeUpdate(sql);
                if (isEqual) {
                    return Response.ok().build();
                }
            } catch (RazorpayException | SQLException e) {
                System.out.println("Exception caused because of " + e.getMessage());
                return Response.status(Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Status.BAD_REQUEST).build();
    }
}
