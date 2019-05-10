package nl._42.max_login_attempts_spring_boot_starter.listener;

public class UsernameRemoteAddressBlockedException extends RuntimeException {

    UsernameRemoteAddressBlockedException(String username, String remoteAddr) {
        super("Username " + username + " is now blocked on remote address " + remoteAddr);
    }
}
