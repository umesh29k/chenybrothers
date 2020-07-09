package com.itpaths.artesia.dam.service;

import com.itpaths.artesia.dam.model.Todo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArtesiaPager {
    public List<Todo> retrieveTodos(String user) {
        List<Todo> filteredTodos = new ArrayList<Todo>();
        return filteredTodos;
    }
}