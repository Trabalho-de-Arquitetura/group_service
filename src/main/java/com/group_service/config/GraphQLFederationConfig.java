package com.group_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.group_service.entity.Group;
import com.group_service.repository.GroupRepository;
import graphql.TypeResolutionEnvironment;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class GraphQLFederationConfig {

    private final GroupRepository groupRepository; // Injete o seu repositório

    public GraphQLFederationConfig(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                // Configura o _entities resolver para o tipo User
                // Este é o resolver que o gateway chamará para buscar Users por ID
                .type("Query", builder -> builder.dataFetcher("_entities", env -> {
                    List<Map<String, Object>> representations = env.getArgument("representations");
                    return representations.stream()
                            .map(representation -> {
                                if ("Group".equals(representation.get("__typename"))) {
                                    Object idObject = representation.get("id");
                                    UUID id = null;
                                    if (idObject instanceof String) {
                                        try {
                                            id = UUID.fromString((String) idObject);
                                        } catch (IllegalArgumentException e) {
                                            return null; /* ou log */
                                        }
                                    } else if (idObject instanceof UUID) {
                                        id = (UUID) idObject;
                                    }
                                    if (id != null) {
                                        return groupRepository.findById(id).orElse(null);
                                    }
                                }
                                return null;
                            })
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList());
                }))
                // Se você tiver interfaces ou uniões que precisam ser resolvidas (não é o caso aqui ainda):
                // .type("NomeDaInterfaceOuUniao", builder -> builder.typeResolver(typeResolverParaInterfaceOuUniao()))
                ;
    }

    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer(TypeResolver entityTypeResolver, DataFetcher entitiesDataFetcher) {
        return builder -> builder.schemaFactory((registry, wiring) ->
                Federation.transform(registry, wiring)
                        .fetchEntities(entitiesDataFetcher)
                        .resolveEntityType(entityTypeResolver)
                        .build());
    }

    @Bean
    public TypeResolver entityTypeResolver() {
        return new TypeResolver() {
            @Override
            public GraphQLObjectType getType(TypeResolutionEnvironment env) {
                Object obj = env.getObject();

                // Ajuste conforme suas entidades.
                if (obj instanceof Group) {
                    return env.getSchema().getObjectType("Group");
                }
                // Adicione outros tipos conforme necessário

                return null;
            }
        };
    }

    @Bean
    public DataFetcher entitiesDataFetcher() {
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) {
                List<Map<String, Object>> representations = environment.getArgument("representations");
                List<Object> entities = new ArrayList<>();

                assert representations != null;
                for (Map<String, Object> representation : representations) {
                    String typename = (String) representation.get("__typename");

                    // Ajuste conforme sua entidade federada
                    if ("Group".equals(typename)) {
                        String id_string = (String) representation.get("id");
                        UUID id = UUID.fromString(id_string);
                        Optional<Group> user = groupRepository.findById(id);
                        entities.add(user);
                    }

                }

                return entities;
            }
        };
    }


    // Exemplo de TypeResolver se você tivesse uma interface/união
    /*
    private TypeResolver typeResolverParaInterfaceOuUniao() {
        return env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof TipoConcretoA) {
                return env.getSchema().getObjectType("TipoConcretoA");
            } else if (javaObject instanceof TipoConcretoB) {
                return env.getSchema().getObjectType("TipoConcretoB");
            }
            return null; // ou lança exceção
        };
    }
    */
}