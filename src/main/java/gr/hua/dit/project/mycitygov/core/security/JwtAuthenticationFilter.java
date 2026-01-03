package gr.hua.dit.project.mycitygov.core.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import gr.hua.dit.project.mycitygov.core.model.UserType;

/**
 * JWT authentication filter for /api/**.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService;

	public JwtAuthenticationFilter(final JwtService jwtService) {
		if (jwtService == null)
			throw new NullPointerException();
		this.jwtService = jwtService;
	}

	private void writeError(final HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\":\"invalid_token\"}");
	}

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		final String path = request.getServletPath();
		if (path.startsWith("/api/auth")) {
			return true;
		}
		return !path.startsWith("/api/");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doFilterInternal(final HttpServletRequest request,
			final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String token = authorizationHeader.substring(7);
		try {
			final Claims claims = this.jwtService.parse(token);
			final String email = claims.getSubject();
			final Object userIdClaim = claims.get("userId");
			final Object userTypeClaim = claims.get("userType");
			if (email == null || userIdClaim == null || userTypeClaim == null) {
				throw new IllegalStateException("Missing claims");
			}
			final long userId = Long.parseLong(userIdClaim.toString());
			final UserType userType = UserType.valueOf(userTypeClaim.toString());
			final Collection<String> roles = (Collection<String>) claims.get("roles");
			final ApplicationUserDetails principal = new ApplicationUserDetails(
					userId,
					email,
					"N/A",
					userType);

			final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					principal,
					null,
					roles == null ? principal.getAuthorities()
							: roles.stream().map(r -> "ROLE_" + r).distinct()
									.map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
									.toList());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception ex) {
			LOGGER.warn("JWT authentication failed", ex);
			this.writeError(response);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
