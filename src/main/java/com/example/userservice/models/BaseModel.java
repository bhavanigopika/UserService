package com.example.userservice.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean isDeleted; //initially, it is false

}

/*
When someone log out from the system, then token has expired - token will be invalidated
Token table -> value, user, expiryAt
Once token has expired, then remove that row from the table – so that, when someone trying to find that particular token, it will not present in the table. So that we will say token has expired

2 ways of removing rows from a table
1)	Actual removing the row from the table
2)	Set the isDeleted column to true => soft delete  This means, we did not delete the actual row but have common attributes for all the table (i.e)
    we have 1 common column known as isDeleted
            Initially it is false (i.e) all the rows are not deleted but let’s say If I want to delete a token, I’ll just isDeleted column = true
which means that row isdeleted become true, (i.e) internally not deleted but at outside I make it as isDeleted = true which means the token will get expired (or) it is invalidated.
 */