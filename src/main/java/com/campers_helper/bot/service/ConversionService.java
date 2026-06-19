package com.campers_helper.bot.service;

import com.campers_helper.bot.commands.UnitType;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    public static double convert(UnitType from, UnitType to, double value, double contextVariable) {
        if (from == to) return value;
        if (value <= 0 || contextVariable <= 0) return 0.0;

        return switch (from) {
            case WATT -> switch (to) {
                case AMPERE -> value / contextVariable;
                case VOLT   -> value / contextVariable;
                default     -> value;
            };
            case AMPERE -> switch (to) {
                case WATT -> value * contextVariable;
                case VOLT -> contextVariable / value;
                default   -> value;
            };
            case VOLT -> switch (to) {
                case WATT   -> value * contextVariable;
                case AMPERE -> contextVariable / value;
                default     -> value;
            };
        };
    }
}