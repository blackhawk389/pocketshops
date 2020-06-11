package com.appabilities.sold.custom.chips;

import android.util.Patterns;

import java.io.Serializable;

/**
 * Simple container object for contact data
 *
 * Created by mgod on 9/12/13.
 * @author mgod
 */
public class Person implements Serializable {
    private String name;
    private String email;

    public Person(String n, String e) {
        name = n;
        email = e;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return name; }

    public static Person getPersonFromEmail(String completionText){
        int index = completionText.indexOf('@');
        if ( !Patterns.EMAIL_ADDRESS.matcher(completionText).matches() && index != -1) {
            return new Person(completionText, completionText.replace(" ", "") + ".com");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(completionText).matches() && index == -1) {
            return new Person(completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new Person(completionText.substring(0, index), completionText);
        }
    }
}