package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.Instructor;
import com.isa.cottages.Repository.InstructorRepository;
import com.isa.cottages.Service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public Instructor findById(Long id) throws Exception {
        if (this.instructorRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(Instructor service)");
        }
        return this.instructorRepository.findById(id).get();
    }
}
