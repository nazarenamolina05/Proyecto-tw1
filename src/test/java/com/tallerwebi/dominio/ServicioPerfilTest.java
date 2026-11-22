package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPerfilTest {

    private RepositorioUsuario repositorioUsuarioMock;
    private ServicioPerfil servicioPerfil;

    @BeforeEach
    public void init() {
        this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
        this.servicioPerfil = new ServicioPerfilImpl(this.repositorioUsuarioMock);
    }

    @Test
    public void dadoQueElEmailYaLoUsaOtroUsuarioDeberiaDarError() {
        Usuario usuarioActual = unUsuario(1L, "ana@test.com");
        Usuario otroUsuario = unUsuario(2L, "luis@test.com");
        when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuarioActual);
        when(this.repositorioUsuarioMock.buscar("luis@test.com")).thenReturn(otroUsuario);

        assertThrows(UsuarioExistente.class, () ->
                this.servicioPerfil.actualizarDatos(1L, "Ana", "luis@test.com")
        );
        assertThat(usuarioActual.getEmail(), equalTo("ana@test.com"));
        verify(this.repositorioUsuarioMock, never()).modificar(any());
    }

    @Test
    public void dadoQueElEmailEstaLibreDeberiaActualizarLosDatosYGuardar() throws UsuarioExistente {
        Usuario usuarioActual = unUsuario(1L, "ana@test.com");
        when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuarioActual);
        when(this.repositorioUsuarioMock.buscar("nuevo@test.com")).thenReturn(null);

        this.servicioPerfil.actualizarDatos(1L, "Ana Pérez", "nuevo@test.com");

        assertThat(usuarioActual.getNombreUsuario(), equalTo("Ana Pérez"));
        assertThat(usuarioActual.getEmail(), equalTo("nuevo@test.com"));
        verify(this.repositorioUsuarioMock).modificar(usuarioActual);
    }

    @Test
    public void dadoQueElUsuarioConservaSuEmailDeberiaPoderCambiarSoloElNombre()
            throws UsuarioExistente {
        Usuario usuarioActual = unUsuario(1L, "ana@test.com");
        when(this.repositorioUsuarioMock.buscarPorId(1L)).thenReturn(usuarioActual);
        when(this.repositorioUsuarioMock.buscar("ana@test.com")).thenReturn(usuarioActual);

        this.servicioPerfil.actualizarDatos(1L, "Ana Pérez", "ana@test.com");

        assertThat(usuarioActual.getNombreUsuario(), equalTo("Ana Pérez"));
        verify(this.repositorioUsuarioMock).modificar(usuarioActual);
    }

    private Usuario unUsuario(Long id, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(email);
        return usuario;
    }
}