package com.example.exodia.department.service;

import com.example.exodia.department.domain.Department;
import com.example.exodia.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Department createDepartment(String name, Long parentId) {
        Department parentDepartment = departmentRepository.findById(parentId).orElse(null);
        Department department = new Department(name, parentDepartment);
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, String name, Long parentId) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found"));
        Department parentDepartment = departmentRepository.findById(parentId).orElse(null);
        department.update(name, parentDepartment);
        return departmentRepository.save(department);
    }

    public void updateDepartmentHierarchy(Long departmentId, Long newParentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Department not found"));
        Department newParentDepartment = departmentRepository.findById(newParentId).orElseThrow(() -> new IllegalArgumentException("Parent Department not found"));

        department.setParentDepartment(newParentDepartment);
        departmentRepository.save(department);

        updateChildDepartments(department);
    }

    private void updateChildDepartments(Department parentDepartment) {
        List<Department> childDepartments = departmentRepository.findByParentDepartment_Id(parentDepartment.getId());

        for (Department child : childDepartments) {
            child.setParentDepartment(parentDepartment);
            departmentRepository.save(child);

            updateChildDepartments(child);
        }
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    public int calculateDepth(Department department) {
        int depth = 0;
        Department currentDept = department;

        while (currentDept.getParentDepartment() != null) {
            depth++;
            currentDept = currentDept.getParentDepartment();
        }

        return depth;
    }
}
