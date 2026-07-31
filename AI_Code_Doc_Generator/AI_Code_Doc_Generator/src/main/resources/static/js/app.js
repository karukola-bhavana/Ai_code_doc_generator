document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const repoUrlInput = document.getElementById('repoUrlInput');
    const generateBtn = document.getElementById('generateBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');

    const pipelineStatus = document.getElementById('pipelineStatus');
    const stepUrl = document.getElementById('step-url');
    const stepBackend = document.getElementById('step-backend');
    const stepGithub = document.getElementById('step-github');
    const stepParser = document.getElementById('step-parser');
    const stepGemini = document.getElementById('step-gemini');

    const repoSummarySection = document.getElementById('repoSummarySection');
    const metaName = document.getElementById('metaName');
    const metaOwner = document.getElementById('metaOwner');
    const metaLang = document.getElementById('metaLang');
    const metaStars = document.getElementById('metaStars');
    const metaForks = document.getElementById('metaForks');
    const metaDesc = document.getElementById('metaDesc');

    const statFiles = document.getElementById('statFiles');
    const statClasses = document.getElementById('statClasses');
    const statEndpoints = document.getElementById('statEndpoints');

    const docViewerSection = document.getElementById('docViewerSection');
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabPages = document.querySelectorAll('.tab-page');

    const toggleViewBtn = document.getElementById('toggleViewBtn');
    const copyBtn = document.getElementById('copyBtn');
    const downloadBtn = document.getElementById('downloadBtn');

    // Data Storage
    let docData = null;
    let activeTabId = 'readmeTab';
    let isRawView = false;

    // Set initial marked options
    if (window.marked) {
        marked.setOptions({
            gfm: true,
            breaks: true
        });
    }

    // Step Pipeline Helper
    function resetPipeline() {
        [stepUrl, stepBackend, stepGithub, stepParser, stepGemini].forEach(step => {
            step.classList.remove('active', 'completed');
        });
        pipelineStatus.textContent = 'Idle';
        pipelineStatus.className = 'status-indicator';
    }

    function setStepState(stepElement, state) {
        stepElement.classList.remove('active', 'completed');
        if (state) stepElement.classList.add(state);
    }

    // Tab Navigation
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            tabBtns.forEach(b => b.classList.remove('active'));
            tabPages.forEach(p => p.classList.remove('active'));

            btn.classList.add('active');
            activeTabId = btn.getAttribute('data-tab');
            document.getElementById(activeTabId).classList.add('active');

            updateDownloadButtonText();
        });
    });

    function updateDownloadButtonText() {
        const names = {
            'readmeTab': 'README.md',
            'apiTab': 'API_DOCS.md',
            'archTab': 'ARCHITECTURE.md',
            'commentsTab': 'CODE_COMMENTS.md'
        };
        downloadBtn.innerHTML = `<i class="fa-solid fa-download"></i> Download ${names[activeTabId] || 'README.md'}`;
    }

    // Toggle Raw / Preview
    toggleViewBtn.addEventListener('click', () => {
        isRawView = !isRawView;
        tabPages.forEach(page => {
            const preview = page.querySelector('.md-preview');
            const editor = page.querySelector('.md-editor');
            if (isRawView) {
                preview.classList.add('hidden');
                editor.classList.remove('hidden');
            } else {
                // Sync edits from editor to preview
                if (window.marked) {
                    preview.innerHTML = marked.parse(editor.value);
                }
                preview.classList.remove('hidden');
                editor.classList.add('hidden');
            }
        });
    });

    // Copy Content
    copyBtn.addEventListener('click', () => {
        const activeEditor = document.querySelector(`#${activeTabId} .md-editor`);
        if (activeEditor && activeEditor.value) {
            navigator.clipboard.writeText(activeEditor.value).then(() => {
                const originalHtml = copyBtn.innerHTML;
                copyBtn.innerHTML = '<i class="fa-solid fa-check"></i> Copied!';
                setTimeout(() => copyBtn.innerHTML = originalHtml, 2000);
            });
        }
    });

    // Download Markdown File
    downloadBtn.addEventListener('click', () => {
        const activeEditor = document.querySelector(`#${activeTabId} .md-editor`);
        const content = activeEditor ? activeEditor.value : '';
        const names = {
            'readmeTab': 'README.md',
            'apiTab': 'API_DOCS.md',
            'archTab': 'ARCHITECTURE.md',
            'commentsTab': 'CODE_COMMENTS.md'
        };
        const filename = names[activeTabId] || 'README.md';

        const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    });

    // Main Generate Handler
    generateBtn.addEventListener('click', async () => {
        const repoUrl = repoUrlInput.value.trim();
        if (!repoUrl) {
            alert('Please enter a valid GitHub repository URL.');
            return;
        }

        // Get selected options
        const docTypes = [];
        if (document.getElementById('optReadme').checked) docTypes.push('readme');
        if (document.getElementById('optApi').checked) docTypes.push('api');
        if (document.getElementById('optArch').checked) docTypes.push('architecture');
        if (document.getElementById('optComments').checked) docTypes.push('comments');

        if (docTypes.length === 0) {
            alert('Please select at least one documentation type to generate.');
            return;
        }

        // UI Loading state
        generateBtn.disabled = true;
        btnText.classList.add('hidden');
        btnSpinner.classList.remove('hidden');

        resetPipeline();
        pipelineStatus.textContent = 'Processing...';
        pipelineStatus.className = 'status-indicator running';

        // Step 1: URL Input
        setStepState(stepUrl, 'active');
        await new Promise(r => setTimeout(r, 300));
        setStepState(stepUrl, 'completed');

        // Step 2: Spring Boot Backend
        setStepState(stepBackend, 'active');
        await new Promise(r => setTimeout(r, 300));
        setStepState(stepBackend, 'completed');

        // Step 3 & 4: GitHub & JavaParser
        setStepState(stepGithub, 'active');

        try {
            const response = await fetch('/api/doc/generate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    repoUrl: repoUrl,
                    docTypes: docTypes
                })
            });

            setStepState(stepGithub, 'completed');
            setStepState(stepParser, 'active');
            await new Promise(r => setTimeout(r, 400));
            setStepState(stepParser, 'completed');

            // Step 5: Gemini AI
            setStepState(stepGemini, 'active');

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Failed to generate documentation');
            }

            docData = await response.json();

            setStepState(stepGemini, 'completed');
            pipelineStatus.textContent = 'Completed!';
            pipelineStatus.className = 'status-indicator success';

            // Populate Repository & Analysis Summary
            populateSummary(docData);

            // Populate Tabs Content
            populateDocContents(docData);

            // Show sections
            repoSummarySection.classList.remove('hidden');
            docViewerSection.classList.remove('hidden');
            docViewerSection.scrollIntoView({ behavior: 'smooth' });

        } catch (err) {
            pipelineStatus.textContent = 'Error';
            pipelineStatus.className = 'status-indicator';
            alert('Error: ' + err.message);
        } finally {
            generateBtn.disabled = false;
            btnText.classList.remove('hidden');
            btnSpinner.classList.add('hidden');
        }
    });

    function populateSummary(data) {
        if (data.repoInfo) {
            metaName.textContent = data.repoInfo.name || '-';
            metaOwner.textContent = data.repoInfo.owner || '-';
            metaLang.textContent = data.repoInfo.language || 'Java';
            metaStars.textContent = data.repoInfo.stars || 0;
            metaForks.textContent = data.repoInfo.forks || 0;
            metaDesc.textContent = data.repoInfo.description || 'No description provided.';
        }

        if (data.analysisSummary) {
            statFiles.textContent = data.analysisSummary.totalFilesAnalyzed || 0;
            statClasses.textContent = data.analysisSummary.totalClassesFound || 0;
            statEndpoints.textContent = data.analysisSummary.totalEndpointsFound || 0;
        }
    }

    function populateDocContents(data) {
        setMarkdownContent('readmePreview', 'readmeEditor', data.readmeMarkdown || 'No README documentation requested.');
        setMarkdownContent('apiPreview', 'apiEditor', data.apiDocMarkdown || 'No API documentation requested.');
        setMarkdownContent('archPreview', 'archEditor', data.architectureMarkdown || 'No Architecture documentation requested.');
        setMarkdownContent('commentsPreview', 'commentsEditor', data.codeCommentsMarkdown || 'No Code Comments requested.');
    }

    function setMarkdownContent(previewId, editorId, markdown) {
        const previewEl = document.getElementById(previewId);
        const editorEl = document.getElementById(editorId);

        editorEl.value = markdown;
        if (window.marked) {
            previewEl.innerHTML = marked.parse(markdown);
        } else {
            previewEl.textContent = markdown;
        }
    }
});
