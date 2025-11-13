/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app-routing.module';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgxServiceModule } from 'ngx-simple-charts/base-service';

export const appConfig: ApplicationConfig = {

  providers: [provideRouter(routes), provideAnimations(), importProvidersFrom(NgxServiceModule.forRoot({
            tokenRefreshPath: "/rest/auth/refreshToken",
            logoutPath: "/rest/auth/logout",
            loginRoute: "/",
        })), provideHttpClient(withInterceptorsFromDi())],
};

/*
// my-library.providers.ts inside the library folder of the services
import { EnvironmentProviders, importProvidersFrom } from '@angular/core';
import { MyLibraryModule } from 'my-library';

export interface MyLibraryConfig {
  apiKey: string;
  featureEnabled?: boolean;
}

export function provideMyLibrary(config: MyLibraryConfig): EnvironmentProviders {
  return importProvidersFrom(MyLibraryModule.forRoot(config));
}
*/