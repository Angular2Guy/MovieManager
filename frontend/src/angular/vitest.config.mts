/// <reference types="vitest" />
import { defineConfig } from "vitest/config";
import angular from "@analogjs/vite-plugin-angular";

export default defineConfig({
  plugins: [angular()],
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["src/test-setup.ts"],
    include: ["src/**/*.spec.ts"],
    reporters: ["default", "junit"],
    outputFile: {
      junit: "reports/junit.xml",
    },
    coverage: {
      provider: "v8",
      reporter: ["text", "html", "lcovonly"],
      reportsDirectory: "./coverage",
    },
  },
});
