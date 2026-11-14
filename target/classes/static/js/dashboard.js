(() => {
    const form = document.getElementById('dashboard-filters');
    if (!form) {
        return;
    }

    const content = document.getElementById('dashboard-content');
    const loading = document.getElementById('dashboard-loading');
    const message = document.getElementById('dashboard-message');
    const apiUrl = form.dataset.apiUrl;
    const filters = {
        status: document.getElementById('filter-status'),
        tipo: document.getElementById('filter-type'),
        tecnico: document.getElementById('filter-technician')
    };

    function setOptions(select, values, placeholder) {
        const selected = select.value;
        select.replaceChildren(new Option(placeholder, ''));
        values.forEach(value => select.add(new Option(value, value)));
        select.value = values.includes(selected) ? selected : '';
    }

    function setText(id, value) {
        document.getElementById(id).textContent = value;
    }

    function formatDate(value) {
        const [year, month, day] = value.split('-');
        return `${day}/${month}/${year}`;
    }

    function formatDateTime(value) {
        return new Intl.DateTimeFormat('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }).format(new Date(value));
    }

    function renderDistribution(containerId, items) {
        const container = document.getElementById(containerId);
        container.replaceChildren();
        if (!items.length) {
            container.innerHTML = '<p class="dashboard-empty">Nenhum chamado no período.</p>';
            return;
        }

        const max = Math.max(...items.map(item => item.quantidade), 1);
        items.forEach(item => {
            const row = document.createElement('div');
            row.className = 'distribution-item';

            const name = document.createElement('span');
            name.className = 'distribution-name';
            name.textContent = item.nome;
            name.title = item.nome;

            const track = document.createElement('div');
            track.className = 'distribution-track';
            track.setAttribute('aria-hidden', 'true');
            const fill = document.createElement('div');
            fill.className = 'distribution-fill';
            fill.style.width = `${Math.max(item.quantidade / max * 100, 1)}%`;
            track.append(fill);

            const count = document.createElement('span');
            count.className = 'distribution-count';
            count.textContent = item.quantidade;
            row.append(name, track, count);
            container.append(row);
        });
    }

    function renderDailyChart(items) {
        const chart = document.getElementById('daily-chart');
        chart.replaceChildren();
        if (!items.length) {
            chart.innerHTML = '<p class="dashboard-empty">Nenhum chamado no período.</p>';
            return;
        }

        const max = Math.max(...items.flatMap(item => [item.recebidos, item.atendidos]), 1);
        const labelStep = Math.max(1, Math.ceil(items.length / 8));
        items.forEach((item, index) => {
            const column = document.createElement('div');
            column.className = 'daily-column';
            const value = document.createElement('span');
            value.className = 'daily-value';
            value.textContent = item.recebidos || item.atendidos
                ? `${item.recebidos}/${item.atendidos}` : '';
            const bars = document.createElement('div');
            bars.className = 'daily-bars';
            const received = document.createElement('div');
            received.className = 'daily-bar received';
            received.style.height = `${Math.max(item.recebidos / max * 100, 2)}%`;
            received.title = `${formatDate(item.data)}: ${item.recebidos} recebido(s)`;
            const attended = document.createElement('div');
            attended.className = 'daily-bar attended';
            attended.style.height = `${Math.max(item.atendidos / max * 100, 2)}%`;
            attended.title = `${formatDate(item.data)}: ${item.atendidos} atendido(s)`;
            bars.append(received, attended);
            const label = document.createElement('span');
            label.className = 'daily-label';
            label.textContent = index % labelStep === 0 || index === items.length - 1
                ? formatDate(item.data) : '';
            column.append(value, bars, label);
            chart.append(column);
        });
        chart.setAttribute('aria-label',
            `Chamados recebidos e atendidos por dia, de ${formatDate(items[0].data)} a ${formatDate(items[items.length - 1].data)}`);
    }

    function renderRecent(items) {
        const container = document.getElementById('recent-tickets');
        container.replaceChildren();
        if (!items.length) {
            container.innerHTML = '<p class="dashboard-empty">Nenhum chamado no período.</p>';
            return;
        }

        items.forEach(ticket => {
            const article = document.createElement('article');
            article.className = 'recent-ticket';
            const heading = document.createElement('div');
            heading.className = 'recent-ticket-heading';
            const title = document.createElement('h3');
            title.className = 'recent-ticket-title';
            title.textContent = ticket.descricao || 'Chamado sem descrição';
            const status = document.createElement('span');
            status.className = 'ticket-status';
            status.textContent = ticket.status || 'Sem status';
            heading.append(title, status);

            const meta = document.createElement('p');
            meta.className = 'recent-ticket-meta';
            meta.textContent = [
                ticket.tipo || 'Tipo não informado',
                ticket.tecnico || 'Técnico não atribuído',
                ticket.dataCriacao ? formatDateTime(ticket.dataCriacao) : 'Sem data'
            ].join(' · ');
            article.append(heading, meta);

            if (ticket.consideracoesTecnicas) {
                const notes = document.createElement('p');
                notes.className = 'recent-ticket-notes';
                const label = document.createElement('strong');
                label.textContent = 'Considerações técnicas: ';
                notes.append(label, document.createTextNode(ticket.consideracoesTecnicas));
                article.append(notes);
            }
            container.append(article);
        });
    }

    async function loadDashboard() {
        const params = new URLSearchParams();
        new FormData(form).forEach((value, key) => {
            if (value) {
                params.append(key, value);
            }
        });
        loading.classList.remove('d-none');
        message.classList.add('d-none');
        try {
            const response = await fetch(`${apiUrl}?${params.toString()}`, {
                headers: { Accept: 'application/json' }
            });
            if (!response.ok) {
                throw new Error(`Não foi possível carregar o dashboard (HTTP ${response.status}).`);
            }
            const data = await response.json();

            if (!form.elements.inicio.value) form.elements.inicio.value = data.inicio;
            if (!form.elements.fim.value) form.elements.fim.value = data.fim;
            setOptions(filters.status, data.statuses, 'Todos os status');
            setOptions(filters.tipo, data.tipos, 'Todos os tipos');
            setOptions(filters.tecnico, data.tecnicos, 'Todos os técnicos');

            setText('metric-total', data.indicadores.total);
            setText('metric-undated', data.chamadosSemData
                ? `${data.chamadosSemData} chamado(s) sem data de criação`
                : 'No período selecionado');
            setText('metric-open', data.indicadores.abertos);
            setText('metric-progress', data.indicadores.emAndamento);
            setText('metric-done', data.indicadores.atendidos);
            setText('metric-cancelled', `${data.indicadores.cancelados} cancelados`);
            const legacyAttended = document.getElementById('dashboard-legacy-attended');
            legacyAttended.textContent = data.chamadosFinalizadosSemDataAtendimento
                ? `${data.chamadosFinalizadosSemDataAtendimento} chamado(s) já finalizado(s) não possuem data de atendimento e não entram na série do gráfico.`
                : '';
            renderDailyChart(data.chamadosPorDia);
            renderDistribution('status-chart', data.chamadosPorStatus);
            renderDistribution('type-chart', data.tiposMaisAtendidos);
            renderRecent(data.chamadosRecentes);
            setText('dashboard-updated',
                `Dados atualizados em ${formatDateTime(data.atualizadoEm)}`);
            content.classList.remove('d-none');
        } catch (error) {
            message.textContent = error.message || 'Falha ao carregar os dados do dashboard.';
            message.classList.remove('d-none');
        } finally {
            loading.classList.add('d-none');
        }
    }

    form.addEventListener('submit', event => {
        event.preventDefault();
        loadDashboard();
    });
    document.getElementById('dashboard-refresh').addEventListener('click', loadDashboard);
    document.getElementById('dashboard-clear').addEventListener('click', () => {
        form.reset();
        loadDashboard();
    });

    loadDashboard();
    window.setInterval(loadDashboard, 60000);
})();
