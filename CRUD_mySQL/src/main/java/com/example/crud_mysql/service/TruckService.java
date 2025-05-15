package com.example.crud_mysql.service;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Truck;
import com.example.crud_mysql.repository.TruckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TruckService {
    private final TruckRepository truckRepository;

    public TruckService(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    public Truck createTruck(Truck truck) {

        return truckRepository.save(truck);
    }

    public void deleteTruck(Long id) {
        truckRepository.findById(id).map(truck -> {
            truck.setLifeCycle(LifeCycle.DELETED);
            return truckRepository.save(truck);
        }).orElseThrow(() -> new RuntimeException("Truck not found with id " + id));
    }

    public Page<Truck> searchTrucks(Pageable pageable) {

        return truckRepository.findAllByLifeCycle(pageable, LifeCycle.READY);
    }

    public Truck updateTruck(Long id, Truck updatedTruck) {
        return truckRepository.findById(id).map(truck -> {
            truck.setChassisNumber(updatedTruck.getChassisNumber());
            truck.setPlate(updatedTruck.getPlate());
            truck.setLifeCycle(LifeCycle.READY); // Ensure it's not deleted
            return truckRepository.save(truck);
        }).orElseThrow(() -> new RuntimeException("Truck not found with id " + id));
    }

}
