package com.example.telurjago.Prevalent;

import com.example.telurjago.Model.Request;
import com.example.telurjago.Model.Seller;
import com.example.telurjago.Model.SellerRequest;
import com.example.telurjago.Model.Users;

public class Prevalent {

    public static Users currentOnlineUser;
    public static Seller currentOnlineSeller;

    public static SellerRequest currentRequest;
    public static Request customerCurrentRequest;

    public static final String UserPhoneKey = "Userphone";
    public static final String UserPasswordKey = "Userpassword";

    public static final String SellerUNameKey = "SellerUsername"; //Useless
    public static final String SellerPasswordKey = "SellerPassword"; //Useless

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST = 71;


    public static String convertCodeToStatus (String code){

        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "Shipping";
        else
            return "Shipped";
    }

}
