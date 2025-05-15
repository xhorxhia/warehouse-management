package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.Truck;
import com.example.crud_mysql.service.TruckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trucks")
public class TruckController {

    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }


    @GetMapping("/search")
    public Page<Truck> searchTrucks(Pageable pageable) {

        return truckService.searchTrucks(pageable);
    }

    @PostMapping("/create")
    public Truck createTruck(@RequestBody Truck truck) {

        return truckService.createTruck(truck);
    }

    @PutMapping("/{id}/edit")
    public Truck updateTruck(@PathVariable Long id, @RequestBody Truck truck) {

        return truckService.updateTruck(id, truck);
    }

    @DeleteMapping("/{id}/soft-delete")
    public void deleteTruck(@PathVariable Long id) {

        truckService.deleteTruck(id);
    }

}
