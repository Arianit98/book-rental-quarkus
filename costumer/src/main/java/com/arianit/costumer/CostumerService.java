package com.arianit.costumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class CostumerService {

    @Transactional(SUPPORTS)
    public List<Costumer> findAllCostumers() {
        return Costumer.listAll();
    }

    @Transactional(SUPPORTS)
    public Costumer findCostumerById(Long id) {
        return Costumer.findById(id);
    }

    public Costumer persistCostumer(@Valid Costumer costumer) {
        costumer.persist();
        return costumer;
    }

    public Costumer updateCostumer(@Valid Costumer costumer) {
        Costumer entity = Costumer.findById(costumer.id);
        if (entity == null) {
            return null;
        }
        entity.name = costumer.name;
        entity.email = costumer.email;
        entity.phone = costumer.phone;
        entity.address = costumer.address;
        entity.age = costumer.age;
        return entity;
    }

    public void deleteCostumer(Long id) {
        Costumer.deleteById(id);
    }
}
