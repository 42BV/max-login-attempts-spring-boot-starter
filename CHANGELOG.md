# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [4.0.0] 2026-07-31
- **Breaking**: usernames are matched case-insensitively (and ignoring surrounding whitespace) by default. Failed attempts, block checks and resets now refer to the same record regardless of how the username was typed in the login form. Applications with case-sensitive usernames (where `Admin` and `admin` are different users) must set the new property `max-login-attempts-starter.case-sensitive-usernames: true` to keep tracking those users separately.
- `resetByUsername` now clears the login attempt records of a user for every remote address, instead of only the first matching entry.
- Successful logins now actually clear the failed attempt counter. The success listener used the toString of the authenticated principal (a `UserDetails` object) instead of its username, so counters kept accumulating until the scheduled cache clear.

## [3.0.0] 2025-12-03
- Upgraded to Java 21, Spring Boot 4

