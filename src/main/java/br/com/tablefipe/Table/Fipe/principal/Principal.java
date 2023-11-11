package br.com.tablefipe.Table.Fipe.principal;

import br.com.tablefipe.Table.Fipe.model.Data;
import br.com.tablefipe.Table.Fipe.model.Models;
import br.com.tablefipe.Table.Fipe.model.Vehicle;
import br.com.tablefipe.Table.Fipe.service.ConvertData;
import br.com.tablefipe.Table.Fipe.service.UseApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
  private Scanner read = new Scanner(System.in);
  private UseApi api = new UseApi();
  private ConvertData convert = new ConvertData();

  private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

  public void showMenu() {
    var menu = """
            \n***Opções***
            Carro
            Moto
            Caminhão
            
            Digite o tipo de veículo que deseja buscar:
            """;

    System.out.println(menu);

    var option = read.nextLine();

    String address = null;

    if (option.toLowerCase().contains("car")) {
      address = URL_BASE + "carros/marcas";
    } else if (option.toLowerCase().contains("mot")) {
      address = URL_BASE + "motos/marcas";
    } else if (option.toLowerCase().contains("cam")) {
      address = URL_BASE + "caminhoes/marcas";
    }

    var json = api.getData(address);

    //System.out.println(json);

    var brand = convert.getList(json, Data.class);

    brand.stream()
            .sorted(Comparator.comparing(Data::codigo))
            .forEach(System.out::println);

    System.out.println("\nInforme o código da marca:");

    var codeBrand = read.nextLine();

    address = address + "/" + codeBrand + "/modelos";
    json = api.getData(address);

    var modelList = convert.getData(json, Models.class);

    System.out.println("\nModelos dessa marca");
    modelList.modelos().stream()
            .sorted(Comparator.comparing(Data::codigo))
            .forEach(System.out::println);

    System.out.println("\nDigite o modelo:");

    var modelVehicle = read.nextLine();

    List<Data> modelsFilter = modelList.modelos().stream()
            .filter(m -> m.nome().toLowerCase().contains(modelVehicle.toLowerCase()))
            .collect(Collectors.toList());

    System.out.println("\nModelos filtrados:");
    modelsFilter.forEach(System.out::println);

    System.out.println("\nDigite o código do modelo:");

    var codeModel = read.nextLine();

    address = address + "/" + codeModel + "/anos";
    json = api.getData(address);

    List<Data> years = convert.getList(json, Data.class);
    List<Vehicle> vehicles = new ArrayList<>();

    for (int i = 0; i < years.size(); i++) {
      var addressYear = address + "/" + years.get(i).codigo();
      json = api.getData(addressYear);
      Vehicle vehicle = convert.getData(json, Vehicle.class);
      vehicles.add(vehicle);
    }

    System.out.println("\nTodos os veículos filtrados:");

    vehicles.stream()
            .sorted(Comparator.comparing(Vehicle::anoModelo))
            .forEach(System.out::println);
  }
}
