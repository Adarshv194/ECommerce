package com.ShopOnline.Buy.online.tokens;

import com.ShopOnline.Buy.online.entities.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class ResetPasswordToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    public ResetPasswordToken() {};

    public ResetPasswordToken(User user) {
        this.user = user;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
        createdDate = new Date(calendar.getTime().getTime());
        calendar.add(Calendar.HOUR,2);
        expiryDate = new Date(calendar.getTime().getTime());
        token = UUID.randomUUID().toString();
    }


}
