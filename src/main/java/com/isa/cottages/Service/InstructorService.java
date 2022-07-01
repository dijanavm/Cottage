package com.isa.cottages.Service;

import com.isa.cottages.Model.Instructor;


public interface InstructorService {

    Instructor findById(Long id) throws Exception;
}
