package com.harbour.data.repositories;

import com.harbour.data.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepo extends JpaRepository<Data,Long> {



}
