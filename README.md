Aplikacja mobilna do zgłaszania i zarządzania awariami urządzeń w ramach systemu PJAID.

MVP (Minimum Viable Product)
- Rejestracja zgłoszenia awarii
- Skanowanie kodu QR
- Lista zgłoszeń użytkownika
- Szczegóły zgłoszenia


Zespół & podział zadań

| Osoba               | Zadania                                                          |
|---------------------|------------------------------------------------------------------|
| Jakub Marcinkowski  | [ANDROID] Formularz zgłoszenia awarii, Lista zgłoszeń użytkownika|
| Dagmara Gibas       | [ANDROID] Szczegóły zgłoszenia                                   |
| Karol Spica         | [ANDROID] Dodanie odczytywanie QR kodu w aplikacji mobilnej      |
------------------------------------------------------------------------------------------


# Instrukcja korzystania z Gita

Zarządzanie kodem źródłowym w zespole projektowym opierało się na systemie kontroli wersji GIT. Poniżej przedstawiono dobre praktyki, które obowiązywały podczas pracy z repozytorium.

## Tworzenie brancha

- Zanim rozpoczniesz pracę nad nową funkcjonalnością lub poprawką, utwórz nowy branch.  
- Dzięki temu nie będziesz ingerować w główną gałąź projektu (main).  
- Każda zmiana powinna być realizowana w osobnym branchu.  
- W przypadku dłuższej pracy commituj zmiany regularnie.

## Pull Request i Code Review

Po zakończeniu pracy nad funkcjonalnością:

1. Przejdź na platformę **GitHub** i utwórz **Pull Request (PR)**.  
2. Jako gałąź roboczą wskaż **swój branch**, jako docelową – **main**.
3. Inni członkowie zespołu dokonują przeglądu kodu (Code Review), który obejmuje:
   - sprawdzenie logiki implementacji,
   - zgodność ze standardami kodowania,
   - weryfikację braku błędów.
4. Wprowadź poprawki zgodnie z uwagami i wykonaj kolejny commit — zmiany zostaną automatycznie dołączone do istniejącego PR.

## Testowanie i scalanie zmian

- Przed zatwierdzeniem PR, dokładnie przetestuj swój branch.
- Upewnij się, że nie występują błędy oraz że wszystkie testy jednostkowe i integracyjne przechodzą poprawnie.
- Po pomyślnym mergowaniu do main, usuń swój branch lokalnie i zdalnie.

## Konwencje nazewnictwa

### Nazwy branchy:

`SCRUM_NUMER_TASKA_krotki_opis`

**Przykład:**  
`SCRUM_4_konfiguracja_bazy`

### Opisy commitów:
```
[SCRUM 4] - krótki opis tego co robią commitowane zmiany
```

## Podsumowanie

1. Twórz nowy branch przed każdą zmianą  
2. Commituj zmiany regularnie  
3. Twórz Pull Requesty po zakończeniu pracy  
4. Przeprowadzaj Code Review przed mergowaniem  
5. Scalaj tylko przetestowany kod  
6. Usuwaj nieaktualne branche

---

# Instrukcja korzystania z Sonara

- Warto do IDE zainstalować sobie wtyczkę **SonarQube for IDE** (dla użytkowników IntelliJ).  
- Wtyczka dodaje do menu kontekstowego opcję:  
  **"Analyze with SonarCube for IDE"**.  
- Po uruchomieniu tej opcji pojawią się **informacje o tym, co należy poprawić w kodzie**.
- Warto mieć ją zainstalowaną lokalnie, ponieważ na GitHub dodane zostały tzw. **GitHub Actions**, które **automatycznie uruchamiają analizę Sonara**.
- Jeśli kod **nie przejdzie analizy pozytywnie**, możliwość jego mergowania zostanie **zablokowana**.

---

## Pełen workflow CI/CD zastosowany w projekcie

![Schemat CICD](image/projectFlow.png)