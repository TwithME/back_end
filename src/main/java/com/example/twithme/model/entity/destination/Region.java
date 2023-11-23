package com.example.twithme.model.entity.destination;

import com.example.twithme.common.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "region")
@Where(clause = "delete_yn = 0")
public class Region extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nation_id")
    private Nation nation;

    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}