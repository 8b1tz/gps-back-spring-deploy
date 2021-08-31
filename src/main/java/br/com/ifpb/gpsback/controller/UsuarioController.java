package br.com.ifpb.gpsback.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpb.gpsback.model.Task;
import br.com.ifpb.gpsback.model.Usuario;
import br.com.ifpb.gpsback.repository.TaskRepository;
import br.com.ifpb.gpsback.repository.UsuarioRepository;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TaskRepository taskRepository;

	// CRIAR USUARIO
	@PostMapping("criarusuario")
	public Usuario novo(@RequestBody Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	// LER TODOS USUARIOS
	@GetMapping("listarusuarios")
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}

	// LER USUARIO ESPECIFICO
	@GetMapping(path = { "lerusuario/{idusu}" })
	public List<Usuario> findById(@PathVariable long idusu) {
		List<Usuario> usuarios = new ArrayList<>();
		usuarioRepository.findById(idusu).map(u -> usuarios.add(u));
		return usuarios;
	}

	// ATUALIZAR USUARIO
	@PutMapping(value = "atualizarusuario/{idusu}")
	public Usuario update(@PathVariable long idusu, @RequestBody Usuario usuario) {

		Usuario usuarioDb = usuarioRepository.findById(idusu).get();

		usuarioDb.setEmail(usuario.getEmail());
		usuarioDb.setName(usuario.getName());
		usuarioDb.setPassword(usuario.getPassword());
		Usuario usuarioAtualizado = usuarioRepository.save(usuarioDb);
		return usuarioAtualizado;

	}

	// DELETAR USUARIO
	@DeleteMapping(path = { "deletarusuario/{idusu}" })
	public ResponseEntity<?> delete(@PathVariable long idusu) {
		return usuarioRepository.findById(idusu).map(record -> {
			usuarioRepository.deleteById(idusu);
			return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.notFound().build());
	}

	// ADICIONAR TASK EM USUARIO
	@PostMapping("adicionartask/{idusu}")
	public Task adicionarTaskEmUsuario(@PathVariable long idusu, @RequestBody Task task) {
		usuarioRepository.findById(idusu).map(u -> {
			task.setUsuario(u);
			u.adicionarTask(task);
			taskRepository.save(task);
			usuarioRepository.save(u);
			return task;
		});
		return null;
	}

	// DELETAR TASK ESPECIFICA DO USUARIO ESPECIFICO
	@DeleteMapping(path = { "{idusu}/removertask/{id}" })
	public ResponseEntity delete(@PathVariable long idusu, @PathVariable long id) {
		return usuarioRepository.findById(idusu).map(u -> {
			u.removerTask(taskRepository.getById(id));
			taskRepository.delete(taskRepository.getById(id));
			return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.notFound().build());
	}

	// LER TASK DO USUARIO
	@GetMapping(path = { "/{idusu}/tasks" })
	public List<Task> findById1(@PathVariable long idusu) {
		return usuarioRepository.findById(idusu).get().getTasks();
	}

	// ATUALIZAR TASK ESPECIFICA DE USUARIO ESPECIFICO
	@PutMapping(value = "{idusu}/atualizartask/{id}")
	public List<Task> update(@PathVariable long idusu, @PathVariable long id, @RequestBody Task task) {

		List<Task> taskUsuario = usuarioRepository.findById(idusu).get().getTasks();
		taskUsuario.forEach(t -> {
			t.setDate(task.getDate());
			t.setDescription(task.getDescription());
			t.setStatus(task.getStatus());
			t.setTitle(task.getTitle());
			taskRepository.save(t);

		});
		return taskUsuario;
	}

	// RETORNA A TASK ESPECIFICA DO USUARIO ESPECIFICO
	@GetMapping(path = { "/{idusu}/tasks/{idtask}" })
	public List<Task> findById2(@PathVariable long idusu, @PathVariable long idtask) {
		List<Task> task = usuarioRepository.findById(idusu).get().getTasks();
		List<Task> tasksParaDevolver = new ArrayList<>();
		task.forEach(t -> {
			if (t.getId() == idtask) {
				tasksParaDevolver.add(t);
			}

		});
		return tasksParaDevolver;
	}

	// RETORNA O USUARIO QUE IRA LOGAR
	@PostMapping("/login")
	public Usuario login(@RequestBody Usuario usuario) {
		Usuario usuarioEmail = usuarioRepository.findByEmail(usuario.getEmail());
		if (usuarioEmail.getPassword().equals(usuario.getPassword())) {
			return usuarioEmail;
		}
		return null;
	}
}
