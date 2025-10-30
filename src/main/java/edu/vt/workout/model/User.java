package edu.vt.workout.model;

import jakarta.persistence.*;

//--
// this model represents a registered user in the system with
// a unique user and password & is mapped to the "users" table
// in the database.
//
// used by: userrepository, authcontroller, usercontroller
//--

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment primary keu
    private Long id;

    @Column(unique = true, nullable = false) // must be unique & not nullable
    private String username;

    @Column(nullable = false) // required field
    private String password;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
