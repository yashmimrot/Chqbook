import io.dropwizard.views.View;

public class PaymentPage extends View {

    private int amount;
    private String razorpayOrderId;

    public PaymentPage(int amount, String razorpayOrderId) {
        super("/page1.html");
        this.amount = amount;
        this.razorpayOrderId = razorpayOrderId;
    }

}