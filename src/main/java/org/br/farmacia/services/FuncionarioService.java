package org.br.farmacia.services;

import java.util.ArrayList;
import java.util.List;

import org.br.farmacia.enums.Cargo;
import org.br.farmacia.enums.TipoSetor;
import org.br.farmacia.models.Beneficios;
import org.br.farmacia.models.Funcionario;
import org.br.farmacia.models.Setor;
import org.br.farmacia.repositories.FuncionarioRepository;

import javax.servlet.ServletContext;

public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(ServletContext context) {
        this.funcionarioRepository = new FuncionarioRepository(context);
    }


    public void inicializarFuncionario(Funcionario funcionario) {
        funcionario.setSetor(definirSetorPorCargo(funcionario.getCargo()));
        funcionario.setBeneficios(calcularBeneficiosPorCargo(funcionario.getCargo()));
    }

    public boolean adicionarFuncionario(Funcionario funcionario) {
        if (funcionario != null) {
            inicializarFuncionario(funcionario);
            return funcionarioRepository.save(funcionario);
        }
        return false;
    }

    public void removerFuncionario(int id) {
        funcionarioRepository.delete(id);
    }

    public boolean editarFuncionario(int id, Funcionario novoFuncionario) {
        Funcionario existente = funcionarioRepository.findById(id);
        if (existente != null) {
            inicializarFuncionario(novoFuncionario); // atualiza setor e benefícios
            novoFuncionario.setId(id); // garante que o ID permaneça o mesmo
            return funcionarioRepository.update(novoFuncionario);
        }
        return false;
    }

    public List<Funcionario> listarFuncionarios() {
        return  funcionarioRepository.findAll();
    }


    private Setor setarTipoSetor(TipoSetor tipoSetor) {
        return new Setor(tipoSetor, new ArrayList<>());
    }

    private Setor definirSetorPorCargo(Cargo cargo) {
        TipoSetor tipoSetor;

        switch (cargo) {
            case GERENTE: tipoSetor = TipoSetor.GERENCIA_FILIAL; break;
            case ATENDENTE: tipoSetor = TipoSetor.ATENDIMENTO_CLIENTE; break;
            case RH: tipoSetor = TipoSetor.GESTAO_PESSOAS; break;
            case FINANCEIRO: tipoSetor = TipoSetor.FINANCEIRO; break;
            case VENDEDOR: tipoSetor = TipoSetor.VENDAS; break;
            case ALMOXARIFE: tipoSetor = TipoSetor.ALMOXARIFADO; break;
            case MOTORISTA: tipoSetor = TipoSetor.TRANSPORTADORAS; break;
            default: throw new IllegalArgumentException("Cargo inválido");
        }

        return setarTipoSetor(tipoSetor);
    }

    private Beneficios calcularBeneficiosPorCargo(Cargo cargo) {
        double valeRefeicao = 300, valeAlimentacao = 300, planoSaude = 3000, planoOdonto = 3000;

        switch (cargo) {
            case GERENTE:
                valeRefeicao *= 2;
                valeAlimentacao *= 2;
                planoSaude *= 1.5;
                planoOdonto *= 1.5;
                break;
            case VENDEDOR:
                valeRefeicao *= 1.5;
                valeAlimentacao *= 1.5;
                break;
            case FINANCEIRO:
            case RH:
                planoSaude *= 1.3;
                break;
            case MOTORISTA:
            case ALMOXARIFE:
                valeAlimentacao *= 1.2;
                break;
            case ATENDENTE:
                // sem alteração
                break;
        }

        return new Beneficios(valeRefeicao, valeAlimentacao, planoSaude, planoOdonto);
    }

    public Funcionario buscarPorId(int idEditar) {
        return funcionarioRepository.findById(idEditar);
    }
}
