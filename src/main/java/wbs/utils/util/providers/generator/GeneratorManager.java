package wbs.utils.util.providers.generator;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.generator.num.*;

import java.util.*;

public final class GeneratorManager {
    private GeneratorManager() {}

    private static final Map<String, RegisteredGenerator> generators = new LinkedHashMap<>();

    static {
        // Actual generators that generate unique patterns
        register("pulse", PulseGenerator::new);

        register("random", RandomGenerator::new);
        setRegex("random", "rand(om)?");

        register("cycle", CycleGenerator::new);
        setRegex("cycle", "cyc(le)?");

        register("pingpong", PingPongGenerator::new);
        setRegex("pingpong", "ping(\\s|-)?pong");

        // Math generators that take multiple providers as args
        register("add", AdditionGenerator::new);

        register("sub", SubtractionGenerator::new);
        setRegex("sub", "sub(tract)?");

        register("mul", MultiplicationGenerator::new);
        setRegex("mul", "mul(tiply)?");

        register("div", DivisionGenerator::new);
        setRegex("div", "div(ide)?");

        register("mod", ModuloGenerator::new);
        setRegex("mod", "mod(ulo)?");

        register("abs", AbsGenerator::new);
    }

    /**
     * Register a {@link DoubleGenerator} under a given id, which is used to
     * identify which generator is being used.
     * @param id The unique id.
     * @param producer The producer to register.
     */
    public static void register(String id, GeneratorProducer producer) {
        generators.put(id.toLowerCase(), new RegisteredGenerator(id, producer));
    }

    public static Set<String> getRegisteredIds() {
        return generators.keySet();
    }

    @Nullable
    public static DoubleGenerator getGenerator(String id, ConfigurationSection section, WbsSettings settings, String directory) {
        RegisteredGenerator generator = generators.get(id.toLowerCase());

        if (generator != null)
            return generator.producer.produce(section, settings, directory);

        for (RegisteredGenerator check : generators.values()) {
            if (check.aliases.contains(id.toLowerCase())) {
                return check.producer.produce(section, settings, directory);
            }
        }

        for (RegisteredGenerator check : generators.values()) {
            if (id.matches(check.regex)) {
                return check.producer.produce(section, settings, directory);
            }
        }

        return null;
    }

    public static boolean addAlias(String id, String alias) {
        RegisteredGenerator generator = generators.get(id);

        if (generator == null) return false;

        generator.addAlias(alias);

        return true;
    }

    public static boolean addAliases(String id, Collection<String> aliases) {
        RegisteredGenerator generator = generators.get(id);

        if (generator == null) return false;

        generator.addAlias(aliases);

        return true;
    }

    public static boolean setRegex(String id, String regex) {
        RegisteredGenerator generator = generators.get(id);

        if (generator == null) return false;

        generator.regex = regex;

        return true;
    }

    private static class RegisteredGenerator {
        private final String id;
        private String regex;
        private final List<String> aliases = new LinkedList<>();
        private final GeneratorProducer producer;

        public RegisteredGenerator(String id, GeneratorProducer producer) {
            this.id = id;
            regex = id;
            this.producer = producer;
        }

        private void addAlias(String alias) {
            aliases.add(alias.toLowerCase());
        }

        private void addAlias(Collection<String> aliases) {
            aliases.forEach(this::addAlias);
        }
    }

    /**
     * Represents a way to produce a given {@link DoubleGenerator} from a Configuration Section.
     */
    @FunctionalInterface
    public interface GeneratorProducer {
        /**
         * Create a generator from a ConfigurationSection, logging errors in the given settings
         * @param section The section where this generator is defined
         * @param settings The settings to log errors against
         * @param directory The path taken through the config to get to this point, for logging purposes
         */
        DoubleGenerator produce(ConfigurationSection section, WbsSettings settings, String directory);
    }
}
