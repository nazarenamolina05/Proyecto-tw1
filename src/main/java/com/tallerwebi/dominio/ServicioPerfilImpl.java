package com.tallerwebi.dominio;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPerfil")
@Transactional(readOnly = true)

public class ServicioPerfilImpl implements ServicioPerfil {
    private final RepositorioUsuario repositorioUsuario;

    public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public void actualizarDatos(Long idUsuario, String nombreUsuario, String email)
            throws UsuarioExistente {
        Usuario usuario = this.repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontrado();
        }

        Usuario conEseEmail = this.repositorioUsuario.buscar(email);
        if (conEseEmail != null && !conEseEmail.getId().equals(idUsuario)) {
            throw new UsuarioExistente();
        }

        usuario.setNombreUsuario(nombreUsuario);
        usuario.setEmail(email);
        this.repositorioUsuario.modificar(usuario);
    }
}