import importlib
from typer.testing import CliRunner

from src.cli.main import app

runner = CliRunner()


def test_cli_single_law(monkeypatch, tmp_path):
    """Test CLI crawl command for single law with mocked crawler."""

    class MockCrawler:
        async def crawl_law(self, identifier, url, law_name):
            return {
                "identifier": identifier,
                "law_name": law_name,
                "url": url,
                "markdown": "mock text",
                "embedding": [0.1],
            }

    def fake_import(name):
        return type("M", (), {"OntarioCrawler": MockCrawler})

    monkeypatch.setattr(importlib, "import_module", fake_import)

    monkeypatch.setattr(
        "src.cli.main.get_link_by_identifier",
        lambda x: {
            "province": "ON",
            "identifier": "ON_FAKE",
            "url": "https://example.com",
            "law_name": "Fake Law",
        },
    )

    monkeypatch.setattr(
        "src.cli.main.get_province_settings",
        lambda p: {
            "crawler_module": "src.crawlers.ontario_crawler",
            "crawler_class": "OntarioCrawler",
        },
    )

    output_path = tmp_path / "out_single.json"

    result = runner.invoke(app, ["ON_FAKE", "--output-json", str(output_path)])

    assert result.exit_code == 0, result.stdout

    data = output_path.read_text(encoding="utf-8")
    assert "Fake Law" in data
    assert "embedding" in data


def test_cli_province_all_laws(monkeypatch, tmp_path):
    """Test CLI crawl for a whole province (all laws) with mocked crawler."""

    # Mock crawler with crawl_all_laws
    class MockCrawler:
        async def crawl_all_laws(self, laws):
            # Return one result per law, embedding stubbed
            return [
                {
                    "identifier": law["identifier"],
                    "law_name": law["law_name"],
                    "url": law["url"],
                    "markdown": f"mock text for {law['identifier']}",
                    "embedding": [0.1],
                }
                for law in laws
            ]

    def fake_import(name):
        return type("M", (), {"OntarioCrawler": MockCrawler})

    monkeypatch.setattr(importlib, "import_module", fake_import)

    # Mock province config
    monkeypatch.setattr(
        "src.cli.main.get_province_settings",
        lambda p: {
            "crawler_module": "src.crawlers.ontario_crawler",
            "crawler_class": "OntarioCrawler",
        },
    )

    # Mock link list for province ON
    monkeypatch.setattr(
        "src.cli.main.get_links_by_province",
        lambda p: [
            {
                "identifier": "ON_ESA",
                "url": "https://example.com/esa",
                "law_name": "Employment Standards Act",
            },
            {
                "identifier": "ON_OHSA",
                "url": "https://example.com/ohsa",
                "law_name": "Occupational Health and Safety Act",
            },
        ],
    )

    output_path = tmp_path / "out_province.json"

    # IMPORTANT: we pass just "ON" – len(identifier) <= 3 triggers crawl_province()
    result = runner.invoke(app, ["ON", "--output-json", str(output_path)])

    assert result.exit_code == 0, result.stdout

    data = output_path.read_text(encoding="utf-8")

    # Saved format is {"results": [ ... ]}
    import json
    parsed = json.loads(data)

    assert "results" in parsed
    assert len(parsed["results"]) == 2

    law_names = [r["law_name"] for r in parsed["results"]]
    assert "Employment Standards Act" in law_names
    assert "Occupational Health and Safety Act" in law_names

    # Ensure embeddings included
    for r in parsed["results"]:
        assert "embedding" in r
        assert r["embedding"] == [0.1]
