package com.newtally.core.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.newtally.core.model.MerchantCounter;
import com.newtally.core.model.Order;
import com.newtally.core.model.Role;
import com.newtally.core.service.BranchCounterService;


@Path("/counters")
public class BranchCounterResource extends BaseResource{
    
    private final BranchCounterService counterServ = ServiceFactory.getInstance().getMerchantCounterService();

    @RolesAllowed({Role.BRANCH_COUNTER})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response currentCounter(@Context HttpServletRequest req) throws IOException {

        CounterDto counter = counterServ.getCounterDetails();
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("Counter has been registered successfully");
        dto.setResponse_data(counter);

        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @GET
    @Path("/<id>")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCounter(@PathParam("id") long id) throws IOException {

        CounterDto counter = counterServ.getCounterById(id);
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("Get Counter details in successfully");
        dto.setResponse_data(counter);

        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/currency/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calculateDiscount(@Context HttpServletRequest req) throws IOException {

        HashMap<String, Object> input = gson.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        
        
        HashMap<String, Object> reponseJson=new HashMap<>();
        reponseJson.put("payment_amount", input.get("payment_amount"));
        List<CurrencyDiscountDto> currencyDiscounts=counterServ.getCurrencyDiscounts((Double)input.get("payment_amount"));
        reponseJson.put("currency_discounts", currencyDiscounts);
        
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("Payment Discounts has been generated successfully");
        dto.setResponse_data(reponseJson);

        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/currency/qrCode")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateQRCode(@Context HttpServletRequest req) throws IOException {

        Order order = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), Order.class);
        
        order=counterServ.createOrder(order);
       
        
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("QR code has been generated successfully");
        dto.setResponse_data(order);

        return Response.ok(gson.toJson(dto)).build();
    }
    
    @RolesAllowed({Role.BRANCH_COUNTER})
    @POST
    @Path("/order/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitOrder(@Context HttpServletRequest req) throws IOException {

        HashMap<String, Object> input = gson_pretty.fromJson(new InputStreamReader(req.getInputStream()), HashMap.class);
        System.out.println(input);
        
        List<Order> orders=counterServ.getOrders(input);
        
        ResponseDto dto=new ResponseDto();
        dto.setResponse_code(0);
        dto.setResponse_message("QR code has been generated successfully");
        dto.setResponse_data(orders);

        return Response.ok(gson.toJson(dto)).build();
    }
}
