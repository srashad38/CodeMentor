const form = document.querySelector("#analysisForm");
const codeInput = document.querySelector("#code");
const languageInput = document.querySelector("#language");
const characterCount = document.querySelector("#characterCount");
const exampleButton = document.querySelector("#exampleButton");
const codeFile = document.querySelector("#codeFile");

const examples = {
    Java: `public class Main {
    public static void main(String[] args) {
        for (int i = 0; i <= 5; i--) {
            System.out.println(i);
        }
    }
}`,
    Python: `def average(numbers):
    total = 0
    for number in numbers:
        total += number
    return total / len(numbers)

print(average([]))`,
    JavaScript: `function findUser(users, id) {
  for (let i = 0; i <= users.length; i++) {
    if (users[i].id === id) return users[i];
  }
}`
};

function updateCount() {
    if (characterCount && codeInput) characterCount.textContent = codeInput.value.length;
}

codeInput?.addEventListener("input", updateCount);
updateCount();

codeInput?.addEventListener("keydown", event => {
    if (event.key === "Tab") {
        event.preventDefault();
        const start = codeInput.selectionStart;
        codeInput.setRangeText("    ", start, codeInput.selectionEnd, "end");
        updateCount();
    }
});

exampleButton?.addEventListener("click", () => {
    const language = languageInput.value;
    codeInput.value = examples[language] || examples.Java;
    updateCount();
    codeInput.focus();
});

codeFile?.addEventListener("change", async () => {
    const file = codeFile.files?.[0];
    if (!file) return;
    if (file.size > 20_000) {
        alert("Please choose a code file smaller than 20,000 characters.");
        codeFile.value = "";
        return;
    }
    codeInput.value = await file.text();
    const extension = file.name.split(".").pop().toLowerCase();
    const languages = { java: "Java", py: "Python", js: "JavaScript", ts: "TypeScript", cpp: "C++", cc: "C++", cs: "C#", go: "Go", rs: "Rust" };
    if (languages[extension]) languageInput.value = languages[extension];
    updateCount();
    codeInput.focus();
});

form?.addEventListener("submit", () => {
    const button = document.querySelector("#analyzeButton");
    button.disabled = true;
    button.querySelector(".button-label").hidden = true;
    button.querySelector(".button-loading").hidden = false;
});

document.querySelectorAll("[data-copy-target]").forEach(button => {
    button.addEventListener("click", async () => {
        const target = document.getElementById(button.dataset.copyTarget);
        await navigator.clipboard.writeText(target.innerText);
        button.textContent = "Copied";
        setTimeout(() => button.textContent = "Copy", 1400);
    });
});

const hintItems = [...document.querySelectorAll(".staged-hints li")];
const revealHintButton = document.querySelector(".reveal-hint");
const hintsViewedInput = document.querySelector("#hintsViewed");
let hintsViewed = Math.min(1, hintItems.length);

revealHintButton?.addEventListener("click", () => {
    const next = hintItems.find(item => item.classList.contains("locked-hint"));
    if (next) {
        next.classList.remove("locked-hint");
        hintsViewed += 1;
        if (hintsViewedInput) hintsViewedInput.value = hintsViewed;
    }
    if (!hintItems.some(item => item.classList.contains("locked-hint"))) {
        revealHintButton.textContent = "All hints revealed";
        revealHintButton.disabled = true;
    }
});

document.querySelector(".show-solution")?.addEventListener("click", () => {
    document.querySelector(".solution-panel")?.classList.remove("is-locked");
});

if (document.querySelector("#results")) {
    document.querySelector("#results").scrollIntoView({ behavior: "smooth", block: "start" });
}
