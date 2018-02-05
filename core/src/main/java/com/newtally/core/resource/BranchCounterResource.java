package com.newtally.core.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.newtally.core.ServiceFactory;
import com.newtally.core.dto.CounterDto;
import com.newtally.core.dto.CurrencyDiscountDto;
import com.newtally.core.dto.ResponseDto;
import com.newtally.core.model.Device;
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Order;
import com.newtally.core.model.Role;
import com.newtally.core.service.BranchCounterService;
import com.newtally.core.service.OrderInvoiceService;


@Path("/counters")
public class BranchCounterResource extends BaseResource{
    
    private final BranchCounterService counterServ = ServiceFactory.getInstance().getMerchantCounterService();
    private final OrderInvoiceService orderInvoiceService = ServiceFactory.getInstance().getOrderInvoiceService();

    @RolesAllowed({Role.BRANCH_COUNTER})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response currentCounter(@Context HttpServletRequest req){
        
        ResponseDto dto=new ResponseDto();
        try {
        CounterDto counter = counterServ.getCounterDetails();
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get the counter details");
        dto.setResponse_data(counter);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the counter details");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_MANAGER, Role.MERCHANT})
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCounter(@PathParam("id") long id) {
        
        ResponseDto dto=new ResponseDto();
        try {
        CounterDto counter = counterServ.getCounterById(id);
        dto.setResponse_code(0);
        dto.setResponse_message("Get Counter details in successfully");
        dto.setResponse_data(counter);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the counter details");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/currency/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calculateDiscount(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        HashMap<String, Object> input = gson.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        
        
        HashMap<String, Object> reponseJson=new HashMap<>();
        reponseJson.put("payment_amount", input.get("payment_amount"));
        List<CurrencyDiscountDto> currencyDiscounts=counterServ.getCurrencyDiscounts((Double)input.get("payment_amount"));
        reponseJson.put("currency_discounts", currencyDiscounts);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Payment Discounts has been generated successfully");
        dto.setResponse_data(reponseJson);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to generate discount details");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/currency/qrCode")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateQRCode(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        Order order = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), Order.class);
        
        order=orderInvoiceService.createOrder(order);
        
        dto.setResponse_code(0);
        dto.setResponse_message("QR code has been generated successfully");
        dto.setResponse_data(order);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to generate QR code details");
            dto.setResponse_data(e.getMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/order/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitOrder(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
        HashMap<String, Object> input = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        
        List<Order> orders=counterServ.getOrders(input);
        
        dto.setResponse_code(0);
        dto.setResponse_message("order submitted successfully");
        dto.setResponse_data(orders);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to submit the order");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @PUT
    @Path("/order/{id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelOrder(@PathParam("id") long id) {

        ResponseDto dto=new ResponseDto();
        try {
        orderInvoiceService.cancelOrders(id);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Cancelled the Order");
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to cancel the order");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTransactions(@Context HttpServletRequest req){

        ResponseDto dto=new ResponseDto();
        try {
        List<Order> orders=counterServ.getAllOrders();
        
        dto.setResponse_code(0);
        dto.setResponse_message("Successfully get all transactions");
        dto.setResponse_data(orders);
        } catch(Exception e) {
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to get the transactions");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @PermitAll
    @GET
    @Path("/bit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBitAmount(@Context HttpServletRequest req){

        ResponseDto dto=new ResponseDto();
        try {
            Double orders=counterServ.getBitCoinCostInINR();
        
            dto.setResponse_code(0);
            dto.setResponse_message("Successfully get all transactions");
            dto.setResponse_data(orders);
            } catch(Exception e) {
                dto.setResponse_code(1);
                dto.setResponse_message("Failed to get the transactions");
                dto.setResponse_data(e.getLocalizedMessage());
            }
        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/device")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitDevice(@Context HttpServletRequest req) {
        
        ResponseDto dto=new ResponseDto();
        try {
            Device device = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), Device.class);
        
            device = counterServ.saveDevice(device);
        
        dto.setResponse_code(0);
        dto.setResponse_message("Device has registered successfully");
        dto.setResponse_data(device);
        } catch(Exception e) {
            e.printStackTrace();
            dto.setResponse_code(1);
            dto.setResponse_message("Failed to register the device");
            dto.setResponse_data(e.getLocalizedMessage());
        }
        return Response.ok(gson_pretty.toJson(dto)).build();
    }
}
