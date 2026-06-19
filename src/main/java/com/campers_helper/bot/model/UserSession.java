package com.campers_helper.bot.model;

import com.campers_helper.bot.commands.ConverterState;
import com.campers_helper.bot.commands.UnitType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "users")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    private String username;

    private String locale;

    @Enumerated(EnumType.STRING)
    private ConverterState converterState;

    @Enumerated(EnumType.STRING)
    private UnitType sourceUnit;

    @Enumerated(EnumType.STRING)
    private UnitType targetUnit;

    private Double primaryValue;
}
