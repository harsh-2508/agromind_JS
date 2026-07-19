package com.agromind.auction.auction.model;

import com.agromind.auction.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="crops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // e.g., "Premium Sharbati Wheat"
    private String type;           // e.g., "Wheat", "Soybean"
    private Double quantityKg;     // Weight of the harvest
    private Double basePrice;      // The absolute minimum starting price

    // Many crops can belong to ONE farmer
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="farmer_id",nullable = false)
    private User farmer;
}
