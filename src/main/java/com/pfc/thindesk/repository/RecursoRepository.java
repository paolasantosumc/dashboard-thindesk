package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.Recurso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecursoRepository extends MongoRepository<Recurso, String> {
}
