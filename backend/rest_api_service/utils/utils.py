"""
service utils
"""

from http import HTTPStatus

def error_handling(reason: str, status_code: HTTPStatus.OK) -> dict:
    """
    prepares error handling schema
    """
    return {
        "reason": reason,
        "status_code": status_code
    }
