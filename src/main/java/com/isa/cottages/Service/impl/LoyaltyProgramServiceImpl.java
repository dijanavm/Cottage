package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.Client;
import com.isa.cottages.Service.LoyaltyProgramService;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyProgramServiceImpl implements LoyaltyProgramService {

    @Override
    public Double calculateClientDiscount(Client client) {
        double maxDiscount = 0.4;
        double discount = 0;

        if (client.getLoyaltyProgram().getGoldPoints() > 5) {
            discount += 0.15;
        }
        if (client.getLoyaltyProgram().getSilverPoints() > 5) {
            discount += 0.1;
        }
        if (client.getLoyaltyProgram().getRegularPoints() > 5) {
            discount += 0.05;
        }

        if (discount > maxDiscount) { discount = maxDiscount; }

        return discount;
    }

}
