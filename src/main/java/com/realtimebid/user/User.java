package com.realtimebid.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/*
 * @Entity
 * Marks this class as a JPA Entity.
 *
 * This means Hibernate/JPA will map this Java class
 * to a table in the database.
 *
 * User object  <---->  users table
 */
@Entity

/*
 * Specifies the name of the database table.
 *
 * Without @Table, JPA may use the class name "User" as the table name.
 *
 * Here we explicitly want: Java class -> User Database table -> users
 */
@Table(name = "users")

/*
 * Lombok @Data automatically generates: - Getters - Setters - toString() -
 * equals() - hashCode()
 * 
 * So we don't need to manually write them.
 */
@Data
public class User {

	/*
	 * @Id Marks this field as the PRIMARY KEY of the users table.
	 */
	@Id

	/*
	 * Automatically generates the ID when a new user is inserted.
	 *
	 * GenerationType.IDENTITY means the database generates the ID, usually using
	 * AUTO_INCREMENT / identity column.
	 *
	 * Example: First user -> id = 1 Second user -> id = 2
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*
	 * @Column(nullable = false, unique = true)
	 *
	 * nullable = false -> Email cannot be NULL in the database.
	 *
	 * unique = true -> Two users cannot register with the same email.
	 *
	 * Example:
	 *
	 * user1 -> bhaskar@gmail.com OK user2 -> bhaskar@gmail.com NOT ALLOWED
	 */
	@Column(nullable = false, unique = true)
	private String email;

	/*
	 * Password of the user.
	 *
	 * nullable = false -> Every user must have a password.
	 *
	 * IMPORTANT: In the actual application, this should contain a HASHED password,
	 * not the user's plain-text password.
	 */
	@Column(nullable = false)
	private String password;

	/*
	 * User's display/name.
	 *
	 * @Column is optional here because JPA can create a normal column automatically
	 * from the field.
	 */
	@Column
	private String name;

	/*
	 * Stores the user's role.
	 *
	 * Role is an enum, for example:
	 *
	 * BUYER SELLER ADMIN
	 *
	 * EnumType.STRING tells Hibernate to store the enum as text in the database.
	 *
	 * Example: role = Role.BUYER
	 *
	 * Database: "BUYER"
	 *
	 * This is safer than EnumType.ORDINAL, which would store: 0, 1, 2...
	 */
	@Enumerated(EnumType.STRING)
	private Role role;

	/*
	 * Automatically stores the date and time when the user record is created.
	 *
	 * Example: 2026-09-26T00:20:15
	 *
	 * @CreationTimestamp is provided by Hibernate. We don't need to manually set
	 * createdAt when creating a new User.
	 */
	@CreationTimestamp
	private LocalDateTime createdAt;
}