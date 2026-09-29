package com.realtimebid.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repository layer for the User entity.
 *
 * This interface is responsible for communicating with
 * the database for User-related operations.
 *
 * We don't need to write SQL queries for basic operations.
 * Spring Data JPA provides them automatically.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/*
	 * Finds a user using their email address.
	 *
	 * Spring Data JPA automatically creates the query based on the method name.
	 *
	 * Internally, it is conceptually similar to:
	 *
	 * SELECT * FROM users WHERE email = ?
	 *
	 * Optional<User> is used because the user may or may not exist with the given
	 * email.
	 *
	 * Example:
	 *
	 * findByEmail("bhaskar@gmail.com")
	 *
	 * User found: Optional<User>
	 *
	 * User not found: Optional.empty()
	 */
	Optional<User> findByEmail(String email);

	/*
	 * Checks whether a user already exists with the given email address.
	 *
	 * Spring Data JPA automatically generates the query from the method name.
	 *
	 * Conceptually:
	 *
	 * SELECT COUNT(*) > 0 FROM users WHERE email = ?
	 *
	 * Returns: true -> email already exists false -> email is available
	 *
	 * This is useful during registration to prevent duplicate accounts.
	 */
	boolean existsByEmail(String email);
}