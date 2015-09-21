/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.entity.movement;

import br.com.webbudget.domain.entity.PersistentEntity;
import java.math.BigDecimal;
import java.util.List;
import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import static javax.persistence.FetchType.EAGER;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A entidade que representa nosso movimento fixo
 * 
 * @author Arthur Gregorio  
 *
 * @version 1.0.0
 * @since 2.1.0, 18/09/2015
 */
@Entity
@Table(name = "fixed_movements")
@ToString(callSuper = true, of = "code")
@EqualsAndHashCode(callSuper = true, of = "code")
public class FixedMovement extends PersistentEntity {

    @Getter
    @Column(name = "code", nullable = false, length = 8, unique = true)
    private String code;
    @Getter
    @Setter
    @NotEmpty(message = "{fixed-movement.identification}")
    @Column(name = "description", nullable = false, length = 45)
    private String identification;
    @Getter
    @Setter
    @NotNull(message = "{fixed-movement.value}")
    @Column(name = "value", nullable = false)
    private BigDecimal value;
    @Getter
    @Setter
    @Column(name = "quotes")
    private int quotes;
        
    @Getter
    @Setter
    @Enumerated
    @Column(name = "fixed_movement_type", nullable = false)
    private FixedMovementType fixedMovementType;
    
    /**
     * Fetch eager pois sempre que precisarmos pesquisar um movimento, vamos
     * precisar saber como ele foi distribuido, ou seja, precisaremos do rateio
     */
    @Getter
    @Setter
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "fixedMovement", fetch = EAGER, cascade = REMOVE)
    private List<Apportionment> apportionments;
    
    /**
     * Inicializamos o que for necessario
     */
    public FixedMovement() {
        this.code = this.createApportionmentCode();
    }

    /**
     * @return o codigo unico gerado por marca de tempo para esta entidade
     */
    private String createApportionmentCode() {

        long decimalNumber = System.nanoTime();

        String generated = "";
        final String digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        synchronized (this.getClass()) {

            int mod;
            int authCodeLength = 0;

            while (decimalNumber != 0 && authCodeLength < 5) {

                mod = (int) (decimalNumber % 36);
                generated = digits.substring(mod, mod + 1) + generated;
                decimalNumber = decimalNumber / 36;
                authCodeLength++;
            }
        }
        return generated;
    }
}