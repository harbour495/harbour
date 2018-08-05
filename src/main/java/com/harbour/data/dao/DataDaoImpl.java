package com.harbour.data.dao;

import com.harbour.data.model.Data;
import com.harbour.data.repositories.DataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataDaoImpl implements DataDao {

    @Autowired
    DataRepo dataRepo;
    @Override
    public void insertData(Data data) {
        dataRepo.save(data);
    }
}
