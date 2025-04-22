package com.storyteller.platform.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.storyteller.platform.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {

		String path = request.getRequestURI();
		String method = request.getMethod();
		logger.debug("Request: {} {}", method, path);
		
		String token = getTokenFromRequest(request);

		if (token != null) {
			logger.debug("Token encontrado en la solicitud");
			if (jwtService.validateToken(token)) {
				String username = jwtService.getUsernameFromToken(token);
				logger.debug("Token validado para el usuario: {}", username);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.debug("Autenticación establecida en el contexto de seguridad");
			} else {
				logger.warn("Token no válido para la ruta: {} {}", method, path);
			}
		} else {
			logger.debug("No se encontró token para la ruta: {} {}", method, path);
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		
		// Skip OPTIONS requests to allow CORS preflight
		boolean isOptions = HttpMethod.OPTIONS.matches(request.getMethod());
		
		// Skip file access paths to allow public access to image files
		boolean isFileAccess = path.startsWith("/files/");
		
		if (isOptions) {
			logger.debug("Omitiendo filtro para solicitud OPTIONS: {}", path);
		}
		
		if (isFileAccess) {
			logger.debug("Omitiendo filtro para acceso a archivos: {}", path);
		}
		
		return isOptions || isFileAccess;
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
